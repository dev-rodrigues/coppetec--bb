package br.com.ufrj.coppetecpagamentos.domain.service

import br.com.ufrj.coppetecpagamentos.domain.model.API
import br.com.ufrj.coppetecpagamentos.domain.model.CreateLogRequestDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBConsultaTransferenciaResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBDevolucaoTransferenciaEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBEstadoTransferenciaEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBLoteRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBTransferenciaEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBDevolucaoTransferenciaEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBLoteEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBTransferenciaEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.Objects.nonNull

@Service
class ConsultarLoteService(
        private val bbPort: BBPort,
        private val bBLoteRepository: BBLoteRepository,
        private val bbTransferenciasRepository: BBTransferenciaEntityRepository,
        private val bBEstadoTransferenciaEntityRepository: BBEstadoTransferenciaEntityRepository,
        private val bBDevolucaoTransferenciaEntityRepository: BBDevolucaoTransferenciaEntityRepository,
        private val logClient: LogClient,
) {

    private val logger: Logger = LoggerFactory.getLogger(ConsultarLoteService::class.java)

    fun executar(lote: BBLoteEntity, step: Int, header: BigInteger) {

        logger.info("STEP ${step}: CONSULTANDO LOTE {}", lote.id!!)

        logClient.createLog(
                CreateLogRequestDto(
                        header = header,
                        aplicacao = 1,
                        classe = this::class.java.simpleName,
                        metodo = "executar",
                        parametros = "$lote",
                        usuarioCodigo = null,
                        usuarioNome = null,
                        criticalidade = 1,
                        servico = 1,
                        mensagemDeErro = "STEP ${step}: CONSULTANDO LOTE ${lote.id!!}"
                )
        )

        val token = bbPort.autenticar(
                api = API.TRANSFERENCIA,
                header = header
        ).body!!.accessToken

        val loteConsultado = bbPort.consultarLote(
                idLote = lote.id!!,
                accessToken = token,
                header = header
        )?.body

        logClient.createLog(
                CreateLogRequestDto(
                        header = header,
                        aplicacao = 1,
                        classe = this::class.java.simpleName,
                        metodo = "executar",
                        parametros = "$loteConsultado",
                        usuarioCodigo = null,
                        usuarioNome = null,
                        criticalidade = 1,
                        servico = 1,
                        mensagemDeErro = "STEP ${step}: LOTE CONSULTADO ${loteConsultado}"
                )
        )

        if (loteConsultado != null) {
            val loteAtualizado = bBLoteRepository.save(
                    lote.atualizarBBLoteEntityComConsulta(loteConsultado)
            )

            logClient.createLog(
                    CreateLogRequestDto(
                            header = header,
                            aplicacao = 1,
                            classe = this::class.java.simpleName,
                            metodo = "executar",
                            parametros = "$loteAtualizado",
                            usuarioCodigo = null,
                            usuarioNome = null,
                            criticalidade = 1,
                            servico = 1,
                            mensagemDeErro = "STEP ${step}: LOTE ATUALIZADO COM CONSULTA ${loteAtualizado}"
                    )
            )

            val transferenciasDoLote = bbTransferenciasRepository.findAllByLote(loteAtualizado.id!!)

            logClient.createLog(
                    CreateLogRequestDto(
                            header = header,
                            aplicacao = 1,
                            classe = this::class.java.simpleName,
                            metodo = "executar",
                            parametros = "$transferenciasDoLote",
                            usuarioCodigo = null,
                            usuarioNome = null,
                            criticalidade = 1,
                            servico = 1,
                            mensagemDeErro = "STEP ${step}: TRANSFERENCIAS DO LOTE ${transferenciasDoLote}"
                    )
            )

            transferenciasDoLote.forEach {

                if (it.identificadorTransferencia != null) {
                    logger.info("STEP ${step}: CONSULTANDO TRANSFERENCIA ${it.id!!} DO LOTE ${loteAtualizado.id!!}")

                    val tokenTransferencia = bbPort.autenticar(
                            header = header,
                    ).body!!.accessToken

                    val bbTransferencia = bbPort.consultarTransferencia(
                            identificadorTransferencia = it.identificadorTransferencia!!,
                            accessToken = tokenTransferencia,
                            header = header
                    )

                    logClient.createLog(
                            CreateLogRequestDto(
                                    header = header,
                                    aplicacao = 1,
                                    classe = this::class.java.simpleName,
                                    metodo = "executar",
                                    parametros = "$bbTransferencia",
                                    usuarioCodigo = null,
                                    usuarioNome = null,
                                    criticalidade = 1,
                                    servico = 1,
                                    mensagemDeErro = "STEP ${step}: TRANSFERENCIA ${it.id!!} DO LOTE ${loteAtualizado.id!!} CONSULTADA"
                            )
                    )

                    if (bbTransferencia != null) {
                        processaTransferencia(
                                step = step,
                                transferencia = it,
                                lote = loteAtualizado.id!!,
                                bbTransferencia = bbTransferencia,
                                header = header
                        )
                    } else {
                        logClient.createLog(
                                CreateLogRequestDto(
                                        header = header,
                                        aplicacao = 1,
                                        classe = this::class.java.simpleName,
                                        metodo = "executar",
                                        parametros = "$it",
                                        usuarioCodigo = null,
                                        usuarioNome = null,
                                        criticalidade = 2,
                                        servico = 1,
                                        mensagemDeErro = "STEP ${step}: TRANSFERENCIA ${it.id!!} DO LOTE ${loteAtualizado.id!!} NÃO POSSUI bbTransferencia"
                                )
                        )
                        logger.error(
                                "STEP ${step}: TRANSFERENCIA ${it.id!!} DO LOTE ${loteAtualizado.id!!} NÃO ENCONTRADA"
                        )
                    }
                } else {
                    logClient.createLog(
                            CreateLogRequestDto(
                                    header = header,
                                    aplicacao = 1,
                                    classe = this::class.java.simpleName,
                                    metodo = "executar",
                                    parametros = "$it",
                                    usuarioCodigo = null,
                                    usuarioNome = null,
                                    criticalidade = 2,
                                    servico = 1,
                                    mensagemDeErro = "STEP ${step}: TRANSFERENCIA ${it.id!!} DO LOTE ${loteAtualizado.id!!} NÃO ENCONTRADA"
                            )
                    )
                }
            }
        } else {
            logger.error("STEP ${step}: LOTE ${lote.id!!} NÃO ENCONTRADO")
        }
    }

    fun processaTransferencia(
            step: Int,
            transferencia: BBTransferenciaEntity,
            lote: BigInteger,
            bbTransferencia: BBConsultaTransferenciaResponseDto,
            header: BigInteger,
    ) {
        logClient.createLog(
                CreateLogRequestDto(
                        header = header,
                        aplicacao = 1,
                        classe = this::class.java.simpleName,
                        metodo = "processaTransferencia",
                        parametros = "$transferencia",
                        usuarioCodigo = null,
                        usuarioNome = null,
                        criticalidade = 1,
                        servico = 1,
                        mensagemDeErro = "STEP ${step}: TRANSFERENCIA ${transferencia.id!!} DO LOTE $lote CONSULTADA COM SUCESSO - ESTADO PAGAMENTO ${bbTransferencia.estadoPagamento}"
                )
        )


        val estadoPagamento = bBEstadoTransferenciaEntityRepository
                .findByEstadoPagamentoIgnoreCase(bbTransferencia.estadoPagamento!!)

        val transferenciaDbAtualizada = transferencia.atualizarTransferenciaComConsulta(
                bbTransferencia = bbTransferencia,
                estadoPagamento = estadoPagamento
        )

        bbTransferenciasRepository.save(transferenciaDbAtualizada)

        logClient.createLog(
                CreateLogRequestDto(
                        header = header,
                        aplicacao = 1,
                        classe = this::class.java.simpleName,
                        metodo = "processaTransferencia",
                        parametros = "$transferenciaDbAtualizada",
                        usuarioCodigo = null,
                        usuarioNome = null,
                        criticalidade = 1,
                        servico = 1,
                        mensagemDeErro = "STEP ${step}: TRANSFERENCIA ${transferencia.id!!} DO LOTE $lote ATUALIZADA COM SUCESSO"
                )
        )

        val dbTransferenciaDevolucao = bbTransferencia.listaDevolucao?.map { devolucao ->
            BBDevolucaoTransferenciaEntity(
                    id = null,
                    transferenciaId = transferencia.id!!,
                    codigoMotivo = devolucao.codigoMotivo,
                    dataDevolucao = LocalDateTime.now(),
                    valorDevolucao = devolucao.valorDevolucao,
            )
        }

        val devolucoesRegistradas =
                bBDevolucaoTransferenciaEntityRepository.findAllByTransferenciaId(transferencia.id!!)

        if (nonNull(devolucoesRegistradas) && devolucoesRegistradas.isEmpty()) {
            logger.info(
                    "STEP ${step}: SALVANDO DEVOLUCAO DA TRANSFERENCIA ${transferencia.id!!} DO LOTE $lote"
            )

            if (nonNull(dbTransferenciaDevolucao) || dbTransferenciaDevolucao!!.isNotEmpty()) {
                bBDevolucaoTransferenciaEntityRepository.saveAll(dbTransferenciaDevolucao!!)
            }
        }
    }
}