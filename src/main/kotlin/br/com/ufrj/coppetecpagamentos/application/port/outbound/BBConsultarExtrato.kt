package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.service.ExtratoService
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBContasAtivasRepository
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BBConsultarExtrato(
    private val bBContasAtivasRepository: BBContasAtivasRepository,
    private val extratoService: ExtratoService,
    private val meterRegistry: MeterRegistry
) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 0 8 * * *")
    fun execute() {
        val contas = bBContasAtivasRepository.getContas()

        log.info("CONSULTANDO EXTRATO DE ${contas.size} CONTAS")

        if (contas.isNotEmpty()) {
            contas.forEachIndexed { index, conta ->
                log.info("CONSULTANDO EXTRATO DA CONTA ${index + 1} DE ${contas.size}")

                try {
                    val result = extratoService
                        .getExtrato(
                            agencia = conta.agenciaSemDv!!,
                            conta = conta.contaCorrenteSemDv!!,
                            dataInicioSolicitacao = conta.consultaPeriodoDe!!,
                            dataFimSolicitacao = conta.consultaPeriodoAte!!,
                        )

                    result?.let { response ->
                        extratoService.register(conta, response)
                        meterRegistry.counter("bb.consultar.extrato", "status", "success").increment()
                    }
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
    }
}