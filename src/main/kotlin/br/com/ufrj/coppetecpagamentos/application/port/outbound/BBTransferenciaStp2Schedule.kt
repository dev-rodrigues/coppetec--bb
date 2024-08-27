package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.model.Toggle.BB_TRANSFERENCIA_STP2_SCHEDULE
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import br.com.ufrj.coppetecpagamentos.domain.service.ConsultarLoteService
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessType.PRIORITY_PAYMENT_INQUIRY_PROCESS
import br.com.ufrj.coppetecpagamentos.domain.singleton.SchedulerExecutionTracker
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBLoteRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TogglePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BBTransferenciaStp2Schedule(
    private val consultarLoteService: ConsultarLoteService,
    private val bBLoteRepository: BBLoteRepository,
    private val togglePort: TogglePort,
    private val properties: ScheduleProperties,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private var isRunning = false

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
            if (active) {
                logger.info("STEP 2: CONSULTAR LOTES PRIORITÁRIOS")

                SchedulerExecutionTracker.getInstance().recordExecutionStart(PRIORITY_PAYMENT_INQUIRY_PROCESS)

                val lotes = bBLoteRepository.findLotesByEstadoRequisicao()

                logger.info("STEP 2: TOTAL DE LOTES PENDENTES DE CONSULTA {} ", lotes.size)

                lotes.forEach {

                    consultarLoteService.executar(
                        lote = it, step = 2,
                    )
                }

            }
        } finally {
            isRunning = false
            SchedulerExecutionTracker.getInstance().recordExecutionEnd(
                PRIORITY_PAYMENT_INQUIRY_PROCESS
            )
        }
    }
}