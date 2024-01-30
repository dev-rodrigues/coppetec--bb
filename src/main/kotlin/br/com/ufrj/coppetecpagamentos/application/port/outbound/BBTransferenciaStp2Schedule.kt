package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.service.ConsultarLoteService
import br.com.ufrj.coppetecpagamentos.domain.service.EnviarLoteService
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBLoteRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.TransferenciaPendenteDatabase
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.EnvioPendentePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BBTransferenciaStp2Schedule(
    private val consultarLoteService: ConsultarLoteService,
    private val bBLoteRepository: BBLoteRepository
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
        logger.info("STEP 2: CONSULTAR LOTES PRIORITÁRIOS")

        val lotes = bBLoteRepository.findLotesByEstadoRequisicao()

        logger.info("STEP 2: TOTAL DE LOTES PENDENTES DE CONSULTA {} ", lotes.size)

        lotes.forEach {
            consultarLoteService.executar(
                lote = it,
                step = 2
            )
        }
    }
}