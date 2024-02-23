package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.model.Toggle
import br.com.ufrj.coppetecpagamentos.domain.model.Toggle.BB_TRANSFERENCIA_STP2_SCHEDULE
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import br.com.ufrj.coppetecpagamentos.domain.service.ConsultarLoteService
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessType.PRIORITY_PAYMENT_INQUIRY_PROCESS
import br.com.ufrj.coppetecpagamentos.domain.singleton.SchedulerExecutionTracker
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
    private val properties: ScheduleProperties
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val TIME_ZONE = "America/Sao_Paulo"
    }

    @Async
    @Scheduled(
        fixedDelay = 5 * 60 * 1000, zone = TIME_ZONE
    )
    fun step2() {
        val active = properties.schedule && togglePort.isEnabled(BB_TRANSFERENCIA_STP2_SCHEDULE)

        if (active) {
            try {
                logger.info("STEP 2: CONSULTAR LOTES PRIORITÁRIOS")
                SchedulerExecutionTracker.getInstance().recordExecutionStart(PRIORITY_PAYMENT_INQUIRY_PROCESS)

                val lotes = bBLoteRepository.findLotesByEstadoRequisicao()

                logger.info("STEP 2: TOTAL DE LOTES PENDENTES DE CONSULTA {} ", lotes.size)

                lotes.forEach {
                    consultarLoteService.executar(
                        lote = it, step = 2
                    )
                }
            } finally {
                SchedulerExecutionTracker.getInstance().recordExecutionEnd(
                    PRIORITY_PAYMENT_INQUIRY_PROCESS
                )
            }

        } else {
            logger.warn("STEP 2: CONSULTA DE LOTES PRIORITÁRIOS DESABILITADO")
        }
    }
}