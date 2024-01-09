package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.service.ConsultarLoteService
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBLoteRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BBConsultarLoteEnviado(
    private val consultarLoteService: ConsultarLoteService,
    private val bBLoteRepository: BBLoteRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(BBConsultarLoteEnviado::class.java)

    @Scheduled(
        fixedDelay = BBEnviarLoteJob.MINUTO,
        zone = BBEnviarLoteJob.TIME_ZONE
    )
    fun step2() {
        logger.info("STEP 2: CONSULTAR LOTE ENVIADO")

        val lotes = bBLoteRepository.findAllByCustomQuery()


        lotes.forEach {
            consultarLoteService.executar(it)
        }
    }
}