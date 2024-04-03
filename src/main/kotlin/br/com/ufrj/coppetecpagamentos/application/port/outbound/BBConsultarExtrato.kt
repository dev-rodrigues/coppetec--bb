package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.exception.BadRequestExtratoException
import br.com.ufrj.coppetecpagamentos.domain.model.CreateLogRequestDto
import br.com.ufrj.coppetecpagamentos.domain.model.Toggle.BB_EXTRATO_SCHEDULE
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import br.com.ufrj.coppetecpagamentos.domain.service.ExtratoService
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessType.BANK_STATEMENT_INQUIRY_PROCESS
import br.com.ufrj.coppetecpagamentos.domain.singleton.SchedulerExecutionTracker
import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBContasAtivasRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TogglePort
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BBConsultarExtrato(
        private val bBContasAtivasRepository: BBContasAtivasRepository,
        private val extratoService: ExtratoService,
        private val meterRegistry: MeterRegistry,
        private val togglePort: TogglePort,
        private val properties: ScheduleProperties,
        private val logClient: LogClient,
) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)
    private var isRunning = false

    @Async
    @Scheduled(
            fixedDelay = 60 * 1000, zone = BBTransferenciaStp2Schedule.TIME_ZONE
    )
    fun getExtrato() {

        if (isRunning) {
            log.error("STEP 3: JÁ ESTÁ EM EXECUÇÃO")
            return
        }

        try {
            isRunning = true
            val active = properties.schedule && togglePort.isEnabled(BB_EXTRATO_SCHEDULE)
            val headerBody = logClient.getHeader().body

            if (active) {
                SchedulerExecutionTracker.getInstance().recordExecutionStart(BANK_STATEMENT_INQUIRY_PROCESS)

                val contas = bBContasAtivasRepository.getContas()

                logClient.createLog(
                        CreateLogRequestDto(
                                header = headerBody!!.id,
                                aplicacao = 1,
                                classe = this::class.java.simpleName,
                                metodo = "getExtrato",
                                parametros = "$contas",
                                usuarioCodigo = null,
                                usuarioNome = null,
                                criticalidade = 1,
                                servico = 1,
                                mensagemDeErro = "CONSULTANDO EXTRATO DE ${contas.size} CONTAS",
                        )
                )

                log.info("CONSULTANDO EXTRATO DE ${contas.size} CONTAS")

                if (contas.isNotEmpty()) {

                    contas.forEachIndexed { index, conta ->
                        log.info("CONSULTANDO EXTRATO DA CONTA ${index + 1} DE ${contas.size}")
                        logClient.createLog(
                                CreateLogRequestDto(
                                        header = headerBody.id,
                                        aplicacao = 1,
                                        classe = this::class.java.simpleName,
                                        metodo = "getExtrato",
                                        parametros = "$conta",
                                        usuarioCodigo = null,
                                        usuarioNome = null,
                                        criticalidade = 1,
                                        servico = 1,
                                        mensagemDeErro = "CONSULTANDO EXTRATO DA CONTA ${index + 1} DE ${contas.size}",
                                )
                        )

                        try {
                            val result = extratoService
                                    .getExtrato(
                                            agencia = conta.agenciaSemDv!!,
                                            conta = conta.contaCorrenteSemDv!!,
                                            dataInicioSolicitacao = conta.consultaPeriodoDe!!,
                                            dataFimSolicitacao = conta.consultaPeriodoAte!!,
                                            headerBody = headerBody.id,
                                    )

                            extratoService.register(
                                    consulta = conta,
                                    response = result,
                                    headerBody = headerBody.id
                            )

                            meterRegistry.counter("bb.consultar.extrato", "status", "success").increment()


                        } catch (e: BadRequestExtratoException) {
                            extratoService.register(conta, null, headerBody.id)

                        } catch (e: Exception) {

                            log.error(
                                    "ERRO AO CONSULTAR EXTRATO: " +
                                            "AG ${conta.agencia} " +
                                            "CC: ${conta.contaCorrente} - " +
                                            "ERRO: ${e.message}"
                            )
                            meterRegistry.counter("bb.consultar.extrato", "status", "error").increment()
                        }
                    }
                } else {
                    log.info("NENHUMA CONTA ATIVA PARA CONSULTAR EXTRATO")
                    meterRegistry.counter("bb.consultar.extrato", "status", "empty").increment()
                }
            } else {
                log.warn("CONSULTA DE EXTRATO DESABILITADA")
                meterRegistry.counter("bb.consultar.extrato", "status", "disabled").increment()
            }
        } finally {
            isRunning = false
            SchedulerExecutionTracker.getInstance().recordExecutionEnd(
                    BANK_STATEMENT_INQUIRY_PROCESS
            )
        }
    }
}