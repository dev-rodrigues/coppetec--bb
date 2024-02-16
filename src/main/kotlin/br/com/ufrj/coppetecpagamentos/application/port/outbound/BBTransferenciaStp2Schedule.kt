package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.model.Toggle
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import br.com.ufrj.coppetecpagamentos.domain.service.ConsultarLoteService
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
        fixedDelay = 5 * 60 * 1000,
        zone = TIME_ZONE
    )
    fun step2() {
        val active = false //properties.schedule && togglePort.isEnabled(Toggle.BB_TRANSFERENCIA_STP2_SCHEDULE)

        if (active) {
            logger.info("STEP 2: CONSULTAR LOTES PRIORITÁRIOS")

            val lotes = bBLoteRepository.findLotesByEstadoRequisicao()

            logger.info("STEP 2: TOTAL DE LOTES PENDENTES DE CONSULTA {} ", lotes.size)

            lotes.forEach {
                consultarLoteService.executar(
                    lote = it,
                    step = 2
                )
            }
        } else {
            logger.warn("STEP 2: CONSULTA DE LOTES PRIORITÁRIOS DESABILITADO")
        }
    }
}