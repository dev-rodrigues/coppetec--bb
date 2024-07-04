package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.model.CreateLogRequestDto
import br.com.ufrj.coppetecpagamentos.domain.model.Toggle.BB_TRANSFERENCIA_STP3_SCHEDULE
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import br.com.ufrj.coppetecpagamentos.domain.service.ConsultarLoteService
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessType.NON_PRIORITY_PAYMENT_INQUIRY_PROCESS
import br.com.ufrj.coppetecpagamentos.domain.singleton.SchedulerExecutionTracker
import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBLoteRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TogglePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BBTransferenciaStp3Schedule(
    private val consultarLoteService: ConsultarLoteService,
    private val bBLoteRepository: BBLoteRepository,
    private val togglePort: TogglePort,
    private val properties: ScheduleProperties,
    private val logClient: LogClient,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private var isRunning = false

    @Scheduled(fixedDelay = 5 * 60 * 1000, zone = BBTransferenciaStp2Schedule.TIME_ZONE)
    fun step3() {

        if (isRunning) {
            logger.error("STEP 3: JÁ ESTÁ EM EXECUÇÃO")
            return
        }

//        val headerBody = logClient.getHeader().body

        try {
            isRunning = true
            val active = properties.schedule && togglePort.isEnabled(BB_TRANSFERENCIA_STP3_SCHEDULE)

            if (active) {
                SchedulerExecutionTracker.getInstance().recordExecutionStart(NON_PRIORITY_PAYMENT_INQUIRY_PROCESS)
                logger.info("STEP 3: CONSULTAR LOTES NÃO PRIORITÁRIOS")

                val lotes = bBLoteRepository.findLotesByEstadoRequisicao(estados = listOf(4, 5))

//                logClient.createLog(
//                    CreateLogRequestDto(
//                        header = headerBody!!.id,
//                        aplicacao = 4,
//                        classe = this::class.java.simpleName,
//                        metodo = "step3",
//                        parametros = "",
//                        usuarioCodigo = null,
//                        usuarioNome = null,
//                        criticalidade = 3,
//                        servico = 1,
//                        mensagemDeErro = "STEP 3: INÍCIO DO PROCESSO DE CONSULTA DE LOTES NÃO PRIORITÁRIOS",
//                        stackTrace = null
//                    )
//                )

                lotes.forEach {
                    consultarLoteService.executar(
                        lote = it,
                        step = 3,
//                        header = headerBody.id
                    )
                }
            } else {
                logger.warn("STEP 3: CONSULTA DE LOTES NÃO PRIORITÁRIOS DESABILITADO")
//                logClient.createLog(
//                    CreateLogRequestDto(
//                        header = headerBody?.id ?: 0,
//                        aplicacao = 4,
//                        classe = this::class.java.simpleName,
//                        metodo = "step3",
//                        parametros = "",
//                        usuarioCodigo = null,
//                        usuarioNome = null,
//                        criticalidade = 3,
//                        servico = 1,
//                        mensagemDeErro = "STEP 3: CONSULTA DE LOTES NÃO PRIORITÁRIOS DESABILITADO",
//                        stackTrace = null
//                    )
//                )
            }
        } finally {
            isRunning = false
            SchedulerExecutionTracker.getInstance().recordExecutionEnd(NON_PRIORITY_PAYMENT_INQUIRY_PROCESS)
//            logClient.createLog(
//                CreateLogRequestDto(
//                    header = headerBody?.id ?: 0,
//                    aplicacao = 4,
//                    classe = this::class.java.simpleName,
//                    metodo = "step3",
//                    parametros = "",
//                    usuarioCodigo = null,
//                    usuarioNome = null,
//                    criticalidade = 3,
//                    servico = 1,
//                    mensagemDeErro = "STEP 3: FIM DO PROCESSO DE CONSULTA DE LOTES NÃO PRIORITÁRIOS",
//                    stackTrace = null
//                )
//            )
        }
    }
}