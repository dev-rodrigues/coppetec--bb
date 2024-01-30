package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.service.EnviarLoteService
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.TransferenciaPendenteDatabase
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.EnvioPendentePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BBTransferenciaStp1Schedule(
    private val envioPendentePort: EnvioPendentePort,
    private val enviarLoteService: EnviarLoteService,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val parts: Int = 300

    @Async
    @Scheduled(
        fixedDelay = 60 * 1000,
        zone = BBTransferenciaStp2Schedule.TIME_ZONE
    )
    fun step1() {
        val remessas = envioPendentePort.getEnvioPendenteDatabase()

        logger.info("STEP 1: TOTAL DE TRANSFERÊNCIAS PENDENTES DE ENVIO {} ", remessas.size)

        remessas.forEach {

            val transferencias = envioPendentePort.getTransferenciasPendente(
                contaFonte = it.contaOrigem!!,
                tipoPagamento = it.tipoPagamento!!
            )

            val parts: List<List<TransferenciaPendenteDatabase>> = transferencias.chunked(parts)

            logger.info(
                "NUMERO DE TRANSFERENCIAS: {} - GERADO {} PARTES DE TRANSFERENCIA",
                transferencias.size,
                parts.size
            )

            parts.forEach { part ->
                enviarLoteService.executar(it, part)
            }
        }
    }
}