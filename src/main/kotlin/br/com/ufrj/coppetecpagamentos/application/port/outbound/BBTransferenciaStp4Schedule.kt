package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.service.ConsultarLoteService
import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBTransferenciaEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.RemessaEletronicaAtualizacoesTEDRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.Objects.nonNull

@Component
class BBTransferenciaStp4Schedule(
    private val service: ConsultarLoteService,
    private val repository: RemessaEletronicaAtualizacoesTEDRepository,
    private val bbTransferenciasRepository: BBTransferenciaEntityRepository,
    private val bbPort: BBPort,
    private val logClient: LogClient,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private var isRunning = false

    @Scheduled(
        fixedDelay = 5 * 60 * 1000,
        zone = BBTransferenciaStp2Schedule.TIME_ZONE
    )
    fun step4() {

        if (isRunning) {
            logger.error("STEP 4: JÁ ESTÁ EM EXECUÇÃO")
            return
        }

        try {
            isRunning = true
//            val headerBody = logClient.getHeader().body
            val remessas = repository.getRemessasEletronicasAtualizacoesTED()

            if (remessas.isEmpty()) {
                logger.info("STEP 4: NÃO HÁ REMESSAS PARA PROCESSAR")
                return
            }

            remessas.forEach {
                logger.info("STEP 4: PROCESSANDO REMESSA ${it.identificadorTransferencia}")
                val token = bbPort.autenticar()

                val transferencia = bbPort.consultarTransferencia(
                    identificadorTransferencia = it.identificadorTransferencia,
                    accessToken = requireNotNull(token.body?.accessToken),
//                    header = headerBody.id
                )

                if (nonNull(transferencia)) {

                    logger.info("STEP 4: REMESSA ${it.identificadorTransferencia} CONSULTADA COM SUCESSO")
                    bbTransferenciasRepository.findById(it.transferenciaId).ifPresent { transferenciaEntity ->
                        service.processaTransferencia(
                            step = 4,
                            transferencia = transferenciaEntity,
                            lote = transferenciaEntity.lote!!,
                            bbTransferencia = transferencia!!,
//                            header = headerBody.id
                        )
                    }
                }
            }
        } finally {
            isRunning = false
        }
    }
}