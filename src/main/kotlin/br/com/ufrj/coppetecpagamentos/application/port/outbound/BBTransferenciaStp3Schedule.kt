package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.service.ConsultarLoteService
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBLoteRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BBTransferenciaStp3Schedule(
    private val consultarLoteService: ConsultarLoteService,
    private val bBLoteRepository: BBLoteRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Async
    @Scheduled(
        fixedDelay = 30 * 60 * 1000,
        zone = BBTransferenciaStp2Schedule.TIME_ZONE
    )
    fun step3() {
        logger.info("STEP 3: CONSULTAR LOTES NÃO PRIORITÁRIOS")

        val lotes = bBLoteRepository.findLotesByEstadoRequisicao(
            estados = listOf(1, 2, 8, 10)
        )

        lotes.forEach {
            consultarLoteService.executar(
                lote = it,
                step = 3
            )
        }
    }
}