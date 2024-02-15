package br.com.ufrj.coppetecpagamentos.domain.service

import br.com.ufrj.coppetecpagamentos.domain.model.API
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBDevolucaoTransferenciaEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBEstadoTransferenciaEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBLoteRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBTransferenciaEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBDevolucaoTransferenciaEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBLoteEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Objects.nonNull

@Service
class ConsultarLoteService(
    private val bbPort: BBPort,
    private val bBLoteRepository: BBLoteRepository,
    private val bbTransferenciasRepository: BBTransferenciaEntityRepository,
    private val bBEstadoTransferenciaEntityRepository: BBEstadoTransferenciaEntityRepository,
    private val bBDevolucaoTransferenciaEntityRepository: BBDevolucaoTransferenciaEntityRepository,
) {

    private val logger: Logger = LoggerFactory.getLogger(ConsultarLoteService::class.java)

    fun executar(lote: BBLoteEntity, step: Int) {

        logger.info("STEP ${step}: CONSULTANDO LOTE {}", lote.id!!)

        val token = bbPort.autenticar(api = API.TRANSFERENCIA).body!!.accessToken

        val loteConsultado = bbPort.consultarLote(
            idLote = lote.id!!,
            accessToken = token
        )?.body

        if (loteConsultado != null) {
            val loteAtualizado = bBLoteRepository.save(
                lote.atualizarBBLoteEntityComConsulta(loteConsultado)
            )

            logger.info("STEP ${step}: LOTE ATUALIZADO COM CONSULTA {}", loteAtualizado.toString())

            val transferenciasDoLote = bbTransferenciasRepository.findAllByLote(loteAtualizado.id!!)

            transferenciasDoLote.forEach {

                if (it.identificadorTransferencia != null) {
                    logger.info("STEP ${step}: CONSULTANDO TRANSFERENCIA ${it.id!!} DO LOTE ${loteAtualizado.id!!}")

                    val tokenTransferencia = bbPort.autenticar().body!!.accessToken

                    val bbTransferencia = bbPort.consultarTransferencia(
                        identificadorTransferencia = it.identificadorTransferencia!!,
                        accessToken = tokenTransferencia
                    )

                    if (bbTransferencia != null) {
                        logger.info(
                            "STEP ${step}: TRANSFERENCIA ${it.id!!} DO LOTE ${loteAtualizado.id!!} CONSULTADA COM SUCESSO"
                        )

                        val estadoPagamento = bBEstadoTransferenciaEntityRepository
                            .findByEstadoPagamentoIgnoreCase(bbTransferencia.estadoPagamento!!)

                        val transferenciaDbAtualizada = it.atualizarTransferenciaComConsulta(
                            bbTransferencia = bbTransferencia,
                            estadoPagamento = estadoPagamento
                        )
                        logger.info(
                            "STEP ${step}: TRANSFERENCIA ${it.id!!} DO LOTE ${loteAtualizado.id!!} ATUALIZADA COM SUCESSO"
                        )

                        bbTransferenciasRepository.save(transferenciaDbAtualizada)

                        val dbTransferenciaDevolucao = bbTransferencia.listaDevolucao?.map { devolucao ->
                            BBDevolucaoTransferenciaEntity(
                                id = null,
                                transferenciaId = it.id!!,
                                codigoMotivo = devolucao.codigoMotivo,
                                dataDevolucao = LocalDateTime.now(),
                                valorDevolucao = devolucao.valorDevolucao,
                            )
                        }

                        val devolucoesRegistradas =
                            bBDevolucaoTransferenciaEntityRepository.findAllByTransferenciaId(it.id!!)

                        if (nonNull(devolucoesRegistradas) && devolucoesRegistradas.isEmpty()) {
                            logger.info(
                                "STEP ${step}: SALVANDO DEVOLUCAO DA TRANSFERENCIA " +
                                        "${it.id!!} DO LOTE ${loteAtualizado.id!!}"
                            )

                            if (nonNull(dbTransferenciaDevolucao) || dbTransferenciaDevolucao!!.isNotEmpty()) {
                                bBDevolucaoTransferenciaEntityRepository.saveAll(dbTransferenciaDevolucao!!)
                            }
                        }
                    } else {
                        logger.error(
                            "STEP ${step}: TRANSFERENCIA ${it.id!!} DO LOTE ${loteAtualizado.id!!} NÃO ENCONTRADA"
                        )
                    }
                } else {
                    logger.error(
                        "STEP ${step}: TRANSFERENCIA ${it.id!!} " +
                                "DO LOTE ${loteAtualizado.id!!} NÃO POSSUI IDENTIFICADOR DE TRANSFERENCIA"
                    )
                }
            }
        } else {
            logger.error("STEP ${step}: LOTE ${lote.id!!} NÃO ENCONTRADO")
        }
    }
}