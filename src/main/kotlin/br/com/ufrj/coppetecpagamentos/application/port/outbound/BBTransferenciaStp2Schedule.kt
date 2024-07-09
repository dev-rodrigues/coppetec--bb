package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.model.CreateLogRequestDto
import br.com.ufrj.coppetecpagamentos.domain.model.Toggle.BB_TRANSFERENCIA_STP2_SCHEDULE
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import br.com.ufrj.coppetecpagamentos.domain.service.ConsultarLoteService
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessType.PRIORITY_PAYMENT_INQUIRY_PROCESS
import br.com.ufrj.coppetecpagamentos.domain.singleton.SchedulerExecutionTracker
import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBLoteRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TogglePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BBTransferenciaStp2Schedule(
    private val consultarLoteService: ConsultarLoteService,
    private val bBLoteRepository: BBLoteRepository,
    private val togglePort: TogglePort,
    private val properties: ScheduleProperties,
    private val logClient: LogClient,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private var isRunning = true

    companion object {
        const val TIME_ZONE = "America/Sao_Paulo"
    }

    @Scheduled(
        fixedDelay = 1 * 60 * 1000, zone = TIME_ZONE
    )
    fun step2() {

        if (isRunning) {
            logger.error("STEP 3: JÁ ESTÁ EM EXECUÇÃO")
            return
        }

        try {
            val active = properties.schedule && togglePort.isEnabled(BB_TRANSFERENCIA_STP2_SCHEDULE)
//            val headerBody = logClient.getHeader().body

            if (active) {
                logger.info("STEP 2: CONSULTAR LOTES PRIORITÁRIOS")
//                logClient.createLog(
//                    CreateLogRequestDto(
//                        header = headerBody!!.id,
//                        aplicacao = 1,
//                        classe = this::class.java.simpleName,
//                        metodo = "step2",
//                        parametros = "",
//                        usuarioCodigo = null,
//                        usuarioNome = null,
//                        criticalidade = 1,
//                        servico = 1,
//                        mensagemDeErro = "STEP 2: CONSULTAR LOTES PRIORITÁRIOS",
//                        stackTrace = null
//                    )
//                )

                SchedulerExecutionTracker.getInstance().recordExecutionStart(PRIORITY_PAYMENT_INQUIRY_PROCESS)

                val lotes = bBLoteRepository.findLotesByEstadoRequisicao()

//                logClient.createLog(
//                    CreateLogRequestDto(
//                        header = headerBody.id,
//                        aplicacao = 1,
//                        classe = this::class.java.simpleName,
//                        metodo = "step2",
//                        parametros = "$lotes",
//                        usuarioCodigo = null,
//                        usuarioNome = null,
//                        criticalidade = 1,
//                        servico = 1,
//                        mensagemDeErro = "STEP 2: TOTAL DE LOTES PENDENTES DE CONSULTA ${lotes.size}",
//                        stackTrace = null
//                    )
//                )

                logger.info("STEP 2: TOTAL DE LOTES PENDENTES DE CONSULTA {} ", lotes.size)

                lotes.forEach {

//                    logClient.createLog(
//                        CreateLogRequestDto(
//                            header = headerBody.id,
//                            aplicacao = 1,
//                            classe = this::class.java.simpleName,
//                            metodo = "step2",
//                            parametros = "$it",
//                            usuarioCodigo = null,
//                            usuarioNome = null,
//                            criticalidade = 1,
//                            servico = 1,
//                            mensagemDeErro = "STEP 2: CONSULTANDO LOTE ${it.id}",
//                            stackTrace = null
//                        )
//                    )

                    consultarLoteService.executar(
                        lote = it, step = 2,
//                        header = headerBody.id
                    )
                }

            } else {
//                logClient.createLog(
//                    CreateLogRequestDto(
//                        header = headerBody!!.id,
//                        aplicacao = 1,
//                        classe = this::class.java.simpleName,
//                        metodo = "step2",
//                        parametros = "",
//                        usuarioCodigo = null,
//                        usuarioNome = null,
//                        criticalidade = 3,
//                        servico = 1,
//                        mensagemDeErro = "STEP 2: CONSULTA DE LOTES PRIORITÁRIOS DESABILITADO",
//                        stackTrace = null
//                    )
//                )
            }
        } finally {
            isRunning = false
            SchedulerExecutionTracker.getInstance().recordExecutionEnd(
                PRIORITY_PAYMENT_INQUIRY_PROCESS
            )
        }
    }
}