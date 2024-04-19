package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.model.CreateLogRequestDto
import br.com.ufrj.coppetecpagamentos.domain.model.Toggle.BB_TRANSFERENCIA_STP1_SCHEDULE
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import br.com.ufrj.coppetecpagamentos.domain.service.EnviarLoteService
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessType.PAYMENT_SENDING_PROCESS
import br.com.ufrj.coppetecpagamentos.domain.singleton.SchedulerExecutionTracker
import br.com.ufrj.coppetecpagamentos.domain.singleton.TransferLog
import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.TransferenciaPendenteDatabase
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.EnvioPendentePort
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TogglePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.Objects.nonNull

@Component
class BBTransferenciaStp1Schedule(
        private val enviarLoteService: EnviarLoteService,
        private val envioPendentePort: EnvioPendentePort,
        private val togglePort: TogglePort,
        private val properties: ScheduleProperties,
        private val logClient: LogClient,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val parts: Int = 300
    private var isRunning = true

    @Async
    @Scheduled(
            fixedDelay = 60 * 1000, zone = BBTransferenciaStp2Schedule.TIME_ZONE
    )
    fun step1() {
        if (isRunning) {
            logger.error("STEP 4: JÁ ESTÁ EM EXECUÇÃO")
            return
        }

        val headerBody = logClient.getHeader().body

        try {
            isRunning = true
            SchedulerExecutionTracker.getInstance().recordExecutionStart(PAYMENT_SENDING_PROCESS)
            val active = properties.schedule && togglePort.isEnabled(BB_TRANSFERENCIA_STP1_SCHEDULE)

            if (active) {

                if (nonNull(headerBody)) {

                    logClient.createLog(CreateLogRequestDto(
                            header = headerBody?.id ?: 0,
                            aplicacao = 4,
                            classe = this::class.java.simpleName,
                            metodo = "step1",
                            parametros = "",
                            usuarioCodigo = null,
                            usuarioNome = null,
                            criticalidade = 3,
                            servico = 1,
                            mensagemDeErro = "STEP 1: INICIO DO PROCESSO DE ENVIO DE TRANSFERÊNCIAS PENDENTES",
                            stackTrace = null
                    ))


                    val remessas = envioPendentePort.getEnvioPendenteDatabase()

                    logClient.createLog(CreateLogRequestDto(
                            header = headerBody?.id ?: 0,
                            aplicacao = 4,
                            classe = this::class.java.simpleName,
                            metodo = "step1",
                            parametros = "",
                            usuarioCodigo = null,
                            usuarioNome = null,
                            criticalidade = 3,
                            servico = 1,
                            mensagemDeErro = "STEP 1: TOTAL DE REMESSAS PENDENTES DE ENVIO ${remessas.size}",
                            stackTrace = null
                    ))

                    remessas.forEachIndexed   { index, it ->

                        logClient.createLog(CreateLogRequestDto(
                                header = headerBody!!.id,
                                aplicacao = 4,
                                classe = this::class.java.simpleName,
                                metodo = "step1",
                                parametros = "",
                                usuarioCodigo = null,
                                usuarioNome = null,
                                criticalidade = 3,
                                servico = 1,
                                mensagemDeErro = "STEP 1: PROCESSANDO REMESSA ${index + 1} DE ${remessas.size}",
                                stackTrace = null
                        ))

                        val transferencias = envioPendentePort.getTransferenciasPendente(
                                contaFonte = it.contaOrigem!!, tipoPagamento = it.tipoPagamento!!
                        )


                        logClient.createLog(CreateLogRequestDto(
                                header = headerBody.id,
                                aplicacao = 4,
                                classe = this::class.java.simpleName,
                                metodo = "step1",
                                parametros = "",
                                usuarioCodigo = null,
                                usuarioNome = null,
                                criticalidade = 3,
                                servico = 1,
                                mensagemDeErro = "STEP 1: REMESSA ${index + 1} DE ${remessas.size} - TOTAL DE TRANSFERÊNCIAS ${transferencias.size}",
                                stackTrace = null
                        ))

                        val parts: List<List<TransferenciaPendenteDatabase>> = transferencias.chunked(parts)

                        logClient.createLog(CreateLogRequestDto(
                                header = headerBody.id,
                                aplicacao = 4,
                                classe = this::class.java.simpleName,
                                metodo = "step1",
                                parametros = "",
                                usuarioCodigo = null,
                                usuarioNome = null,
                                criticalidade = 3,
                                servico = 1,
                                mensagemDeErro = "STEP 1: REMESSA ${index + 1} DE ${remessas.size} - GERADO ${parts.size} PARTES DE TRANSFERÊNCIA",
                                stackTrace = null
                        ))

                        parts.forEachIndexed { indexPart, part ->

                            logClient.createLog(CreateLogRequestDto(
                                    header = headerBody.id,
                                    aplicacao = 4,
                                    classe = this::class.java.simpleName,
                                    metodo = "step1",
                                    parametros = "",
                                    usuarioCodigo = null,
                                    usuarioNome = null,
                                    criticalidade = 3,
                                    servico = 1,
                                    mensagemDeErro = "STEP 1: REMESSA ${index + 1} DE ${remessas.size} - PROCESSANDO PARTE ${indexPart + 1} DE ${parts.size}",
                                    stackTrace = null
                            ))

                            enviarLoteService.executar(it, part, headerBody.id)
                        }
                    }
                }


            } else {
                logClient.createLog(CreateLogRequestDto(
                        header = headerBody?.id ?: 0,
                        aplicacao = 4,
                        classe = this::class.java.simpleName,
                        metodo = "step1",
                        parametros = "",
                        usuarioCodigo = null,
                        usuarioNome = null,
                        criticalidade = 3,
                        servico = 1,
                        mensagemDeErro = "STEP 1: ENVIO DE TRANSFERÊNCIAS PENDENTES DESABILITADO",
                        stackTrace = null
                ))
            }
        } finally {
            isRunning = false
            SchedulerExecutionTracker.getInstance().recordExecutionEnd(PAYMENT_SENDING_PROCESS)
            logClient.createLog(CreateLogRequestDto(
                    header = headerBody?.id ?: 0,
                    aplicacao = 4,
                    classe = this::class.java.simpleName,
                    metodo = "step1",
                    parametros = "",
                    usuarioCodigo = null,
                    usuarioNome = null,
                    criticalidade = 3,
                    servico = 1,
                    mensagemDeErro = "STEP 1: PROCESSO DE ENVIO DE TRANSFERÊNCIAS PENDENTES FINALIZADO",
                    stackTrace = null
            ))
        }
    }
}