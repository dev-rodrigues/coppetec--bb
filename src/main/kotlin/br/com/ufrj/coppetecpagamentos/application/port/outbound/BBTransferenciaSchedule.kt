package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.service.ConsultarLoteService
import br.com.ufrj.coppetecpagamentos.domain.service.EnviarLoteService
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBLoteRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.EnvioPendentePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BBTransferenciaSchedule(
    private val envioPendentePort: EnvioPendentePort,
    private val enviarLoteService: EnviarLoteService,
    private val consultarLoteService: ConsultarLoteService,
    private val bBLoteRepository: BBLoteRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val SEGUNDO: Long = 1000
        const val MINUTO: Long = SEGUNDO * 60
        const val TIME_ZONE = "America/Sao_Paulo"
    }

    @Scheduled(
        fixedDelay = MINUTO,
        zone = TIME_ZONE
    )
    fun step1() {
        val remessas = envioPendentePort.getEnvioPendenteDatabase()

        logger.info("STEP 1: TOTAL DE TRANSFERÊNCIAS PENDENTES DE ENVIO {} ", remessas.size)

        remessas.forEach {

            logger.info("REMESSA CONTA FONTE:{} - TIPO DE PAGAMENTO: {}", it.contaOrigem, it.tipoPagamento)

            val transferencias = envioPendentePort.getTransferenciasPendente(it.contaOrigem!!, it.tipoPagamento!!)

            transferencias.forEach { t ->
                logger.info(
                    "STEP 1: BANCO: {} - AG: {} - CONTA_DÉBITO: {} - CONTA_FONTE: {} - TIPO_PAGAMENTO: {}",
                    t.banco,
                    t.agenciaDebito,
                    t.contaDebito,
                    t.contaFonte,
                    t.tipoPagamento
                )
            }

            enviarLoteService.executar(it, transferencias)
        }
    }

    @Scheduled(
        fixedDelay = MINUTO,
        zone = TIME_ZONE
    )
    fun step2() {
        logger.info("STEP 2: CONSULTAR LOTE ENVIADO")

        val lotes = bBLoteRepository.findAllByCustomQuery()

        lotes.forEach {
            consultarLoteService.executar(it)
        }
    }
}