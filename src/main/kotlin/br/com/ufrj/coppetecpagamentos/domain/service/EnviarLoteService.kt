package br.com.ufrj.coppetecpagamentos.domain.service

import br.com.ufrj.coppetecpagamentos.domain.model.CreateLogRequestDto
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessType.PAYMENT_SENDING_PROCESS
import br.com.ufrj.coppetecpagamentos.domain.singleton.SchedulerExecutionTracker
import br.com.ufrj.coppetecpagamentos.domain.singleton.TransferLog
import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
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
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.Objects.nonNull

@Component
class EnviarLoteService(
        private val bBLoteRepository: BBLoteRepository,
        private val bBTransferenciaEntityRepository: BBTransferenciaEntityRepository,
        private val bBTransferenciaErroEntityRepository: BBTransferenciaErroEntityRepository,
        private val bbPort: BBPort,
        private val logClient: LogClient,
) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun executar(
            loteEnvioPendenteDatabase: LoteEnvioPendenteDatabase,
            transferencias: List<TransferenciaPendenteDatabase>,
            headerBody: BigInteger,
    ) {

        val lote = bBLoteRepository.save(
                BBLoteEntity(
                        LocalDateTime.now()
                )
        )

        SchedulerExecutionTracker.getInstance().addLogTransfer(PAYMENT_SENDING_PROCESS, TransferLog(
                message = "STEP 1: LOTE CRIADO COM ID ${lote.id!!}"
        ))

        logClient.createLog(CreateLogRequestDto(
                header = headerBody,
                aplicacao = 4,
                classe = this::class.java.simpleName,
                metodo = "executar",
                parametros = "[${loteEnvioPendenteDatabase}, ${transferencias}, ${headerBody}]",
                usuarioCodigo = null,
                usuarioNome = null,
                criticalidade = 3,
                servico = 1,
                mensagemDeErro = "STEP 1: LOTE CRIADO COM ID ${lote.id!!}",
                stackTrace = null
        ))


        val loteDeEnvio = BBTransferirRequest.mapLoteRequest(loteEnvioPendenteDatabase, lote)

        SchedulerExecutionTracker.getInstance().addLogTransfer(PAYMENT_SENDING_PROCESS, TransferLog(
                message = "STEP 1: LOTE ${lote.id} PREPARADO PARA ENVIO $loteDeEnvio"
        ))

        logClient.createLog(CreateLogRequestDto(
                header = headerBody,
                aplicacao = 4,
                classe = this::class.java.simpleName,
                metodo = "executar",
                parametros = "[${loteDeEnvio}]",
                usuarioCodigo = null,
                usuarioNome = null,
                criticalidade = 3,
                servico = 1,
                mensagemDeErro = "STEP 1: LOTE ${lote.id} PREPARADO PARA ENVIO $loteDeEnvio",
                stackTrace = null
        ))


        val dbTransferencias: MutableList<BBTransferenciaEntity> = mutableListOf()

        transferencias.forEachIndexed { indexT, it ->

            SchedulerExecutionTracker.getInstance().addLogTransfer(PAYMENT_SENDING_PROCESS, TransferLog(
                    message = "STEP 1: GERANDO TRANSFERÊNCIA ${indexT + 1} DE ${transferencias.size}"
            ))

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

        logClient.createLog(CreateLogRequestDto(
                header = headerBody,
                aplicacao = 4,
                classe = this::class.java.simpleName,
                metodo = "executar",
                parametros = "[${dbTransferencias}]",
                usuarioCodigo = null,
                usuarioNome = null,
                criticalidade = 3,
                servico = 1,
                mensagemDeErro = "STEP 1: lote ${lote.id} com ${dbTransferencias.size} transferências",
                stackTrace = null
        ))

        val token = bbPort.autenticar(header = headerBody).body?.accessToken
        
        SchedulerExecutionTracker.getInstance().addLogTransfer(PAYMENT_SENDING_PROCESS, TransferLog(
                message = "STEP 1: TOKEN DE AUTENTICAO GERADO")
        )

        logClient.createLog(
                CreateLogRequestDto(
                        header = headerBody,
                        aplicacao = 4,
                        classe = this::class.java.simpleName,
                        metodo = "executar",
                        parametros = "[${token}]",
                        usuarioCodigo = null,
                        usuarioNome = null,
                        criticalidade = 3,
                        servico = 1,
                        mensagemDeErro = "STEP 1: lote ${lote.id} TOKEN BB OBTIDO COM SUCESSO, INICIANDO ENVIO DO LOTE",
                        stackTrace = null
                ))

        val response = bbPort.transferir(loteDeEnvio, token!!, headerBody)

        if (nonNull(response)) {
            val body = response!!.body!!

            bBLoteRepository.save(lote.atualizarBBLoteEntityComResposta(body))

            logClient.createLog(
                    CreateLogRequestDto(
                            header = headerBody,
                            aplicacao = 4,
                            classe = this::class.java.simpleName,
                            metodo = "executar",
                            parametros = "[${body}]",
                            usuarioCodigo = null,
                            usuarioNome = null,
                            criticalidade = 3,
                            servico = 1,
                            mensagemDeErro = "STEP 1: lote ${lote.id} ATUALIZADO COM RESPOSTA DO BANCO DO BRASIL",
                            stackTrace = null
                    )
            )

            SchedulerExecutionTracker.getInstance().addLogTransfer(PAYMENT_SENDING_PROCESS, TransferLog(
                    message = "STEP 1: LOTE ${lote.id} ATUALIZADO COM RESPOSTA DO BANCO DO BRASIL")
            )

            body.transferencias.forEach { transferenciaBB ->

                logClient.createLog(
                        CreateLogRequestDto(
                                header = headerBody,
                                aplicacao = 4,
                                classe = this::class.java.simpleName,
                                metodo = "executar",
                                parametros = "[${transferenciaBB}]",
                                usuarioCodigo = null,
                                usuarioNome = null,
                                criticalidade = 3,
                                servico = 1,
                                mensagemDeErro = "STEP 1: lote ${lote.id} transferência ${transferenciaBB.documentoDebito} consultando no banco de dados",
                                stackTrace = null
                        )
                )

                val dbTransferencia = bBTransferenciaEntityRepository
                        .findByLoteAndLancamento(
                                lote.id!!,
                                transferenciaBB.documentoDebito!!
                        )


                bBTransferenciaEntityRepository.save(
                        dbTransferencia.atualizarRegistro(transferenciaBB)
                )

                logClient.createLog(
                        CreateLogRequestDto(
                                header = headerBody,
                                aplicacao = 4,
                                classe = this::class.java.simpleName,
                                metodo = "executar",
                                parametros = "[${dbTransferencia}]",
                                usuarioCodigo = null,
                                usuarioNome = null,
                                criticalidade = 3,
                                servico = 1,
                                mensagemDeErro = "STEP 1: lote ${lote.id} transferência ${transferenciaBB.documentoDebito} atualizada com sucesso",
                                stackTrace = null
                        )
                )

                logClient.createLog(
                        CreateLogRequestDto(
                                header = headerBody,
                                aplicacao = 4,
                                classe = this::class.java.simpleName,
                                metodo = "executar",
                                parametros = "[${transferenciaBB.erros}]",
                                usuarioCodigo = null,
                                usuarioNome = null,
                                criticalidade = 3,
                                servico = 1,
                                mensagemDeErro = "STEP 1: lote ${lote.id} transferência ${transferenciaBB.documentoDebito} com ${transferenciaBB.erros.size} erros",
                                stackTrace = null
                        )
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
            SchedulerExecutionTracker.getInstance().addLogTransfer(PAYMENT_SENDING_PROCESS, TransferLog(
                    message = "STEP 1: TIVEMOS UM ERRO AO ENVIAR O LOTE ${lote.id!!}")
            )
            logClient.createLog(
                    CreateLogRequestDto(
                            header = headerBody,
                            aplicacao = 4,
                            classe = this::class.java.simpleName,
                            metodo = "executar",
                            parametros = "[${lote.id!!}]",
                            usuarioCodigo = null,
                            usuarioNome = null,
                            criticalidade = 1,
                            servico = 1,
                            mensagemDeErro = "STEP 1: TIVEMOS UM ERRO AO ENVIAR O LOTE ${lote.id!!} - ${response?.statusCode} - ${response?.body} - LOTE NÃO ENVIADO",
                            stackTrace = null
                    )
            )
        }
    }
}