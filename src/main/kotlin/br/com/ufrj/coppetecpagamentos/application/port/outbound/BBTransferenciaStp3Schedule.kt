package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.model.Toggle
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import br.com.ufrj.coppetecpagamentos.domain.service.ConsultarLoteService
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
    private val properties: ScheduleProperties
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private var isRunning = false


    @Scheduled(
        fixedDelay = 5 * 60 * 1000,
        zone = BBTransferenciaStp2Schedule.TIME_ZONE
    )
    fun step3() {

        if (isRunning) {
            logger.error("STEP 3: JÁ ESTÁ EM EXECUÇÃO")
            return
        }

        try {
            isRunning = true
            val active = properties.schedule && togglePort.isEnabled(Toggle.BB_TRANSFERENCIA_STP3_SCHEDULE)

            if (active) {
                logger.info("STEP 3: CONSULTAR LOTES NÃO PRIORITÁRIOS")

                val lotes = bBLoteRepository.findLotesByEstadoRequisicao(
                    estados = listOf(4, 5)
                )

                lotes.forEach {
                    consultarLoteService.executar(
                        lote = it,
                        step = 3
                    )
                }
            } else {
                logger.warn("STEP 3: CONSULTA DE LOTES NÃO PRIORITÁRIOS DESABILITADO")
            }
        } finally {
            isRunning = false
        }
    }
}