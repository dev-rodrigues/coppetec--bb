package br.com.ufrj.coppetecpagamentos.domain.service

import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.request.BBTransferenciaRequest
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.request.BBTransferirRequest
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBLoteRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBTransferenciaEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBTransferenciaErroEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.Objects.nonNull

@Component
class EnviarLoteService(
    private val bBLoteRepository: BBLoteRepository,
    private val bBTransferenciaEntityRepository: BBTransferenciaEntityRepository,
    private val bBTransferenciaErroEntityRepository: BBTransferenciaErroEntityRepository,
    private val bbPort: BBPort,
) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun executar(
        loteEnvioPendenteDatabase: LoteEnvioPendenteDatabase,
        transferencias: List<TransferenciaPendenteDatabase>
    ) {

        val lote = bBLoteRepository.save(
            BBLoteEntity(
                LocalDateTime.now()
            )
        )

        val loteDeEnvio = BBTransferirRequest.mapLoteRequest(loteEnvioPendenteDatabase, lote)

        val dbTransferencias: MutableList<BBTransferenciaEntity> = mutableListOf()

        transferencias.forEach {

            val dbTransferencia = bBTransferenciaEntityRepository.save(
                BBTransferenciaEntity.criarRegistroTransferencia(
                    lote.id!!,
                    it.documentoDebito!!
                )
            )

            dbTransferencias.plus(dbTransferencia)

            val t = BBTransferenciaRequest.mapTransferenciaRequest(it)

            loteDeEnvio.listaTransferencias += t
        }

        logger.info("STEP 1: LOTE PREPARADO PARA ENVIO {}", loteDeEnvio)

        val token = bbPort.autenticar().body?.accessToken
        logger.info("STEP 1: TOKEN DE ACESSO {}", token)

        val response = bbPort.transferir(loteDeEnvio, token!!)

        if (nonNull(response)) {
            val body = response!!.body!!

            bBLoteRepository.save(lote.atualizarBBLoteEntityComResposta(body))

            body.transferencias.forEach { transferenciaBB ->
                val dbTransferencia = bBTransferenciaEntityRepository
                    .findByLoteAndLancamento(
                        lote.id!!,
                        transferenciaBB.documentoDebito!!
                    )

                bBTransferenciaEntityRepository.save(
                    dbTransferencia.atualizarRegistro(transferenciaBB)
                )

                bBTransferenciaErroEntityRepository.saveAll(
                    transferenciaBB
                        .erros
                        .map { erro ->
                            BBTransferenciaErroEntity(
                                id = null,
                                transferenciaId = dbTransferencia.id!!,
                                codigoErro = erro
                            )
                        }
                )
            }
        } else {
            logger.error("STEP 1: TIVEMOS UM ERRO AO ENVIAR O LOTE ${lote.id!!}")
        }
    }
}