package br.com.ufrj.coppetecpagamentos.infrastruscture.http.middleware

import br.com.ufrj.coppetecpagamentos.domain.adapter.LocalDateTimeAdapter
import br.com.ufrj.coppetecpagamentos.domain.model.CreateLogRequestDto
import br.com.ufrj.coppetecpagamentos.domain.model.HttpUri
import br.com.ufrj.coppetecpagamentos.domain.property.RestTemplateProperties
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessType
import br.com.ufrj.coppetecpagamentos.domain.singleton.SchedulerExecutionTracker
import br.com.ufrj.coppetecpagamentos.domain.singleton.TransferLog
import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.request.BBTransferirRequest
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBLiberarLoteErroResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBTransferenciaResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBLoteLogRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBLoteLogEntity
import com.google.gson.GsonBuilder
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import java.math.BigInteger
import java.net.UnknownHostException
import java.time.LocalDateTime
import java.util.function.Supplier

@Suppress("UNCHECKED_CAST")
@Component
class RestTemplateProxy(
        private val restTemplateProperties: RestTemplateProperties,
        private val bBLoteLogRepository: BBLoteLogRepository,
        private val logClient: LogClient,
) {

    private val logger = LoggerFactory.getLogger(RestTemplateProxy::class.java)
    private val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

    fun <Y> execute(consumer: Supplier<Y>, httpUri: HttpUri, header: BigInteger): Y? {
        var retryCount = 0
        while (true) {
            try {

                logClient.createLog(
                        CreateLogRequestDto(
                                header = header,
                                aplicacao = 4,
                                classe = this::class.java.simpleName,
                                metodo = "execute",
                                parametros = "[${httpUri}]",
                                usuarioCodigo = null,
                                usuarioNome = null,
                                criticalidade = 3,
                                servico = 1,
                                mensagemDeErro = "STEP 1: ENVIANDO REQUISIÇÃO PARA ${httpUri.name}",
                                stackTrace = null
                        )
                )

                val success = consumer.get()

                logClient.createLog(
                        CreateLogRequestDto(
                                header = header,
                                aplicacao = 4,
                                classe = this::class.java.simpleName,
                                metodo = "execute",
                                parametros = "[${success}]",
                                usuarioCodigo = null,
                                usuarioNome = null,
                                criticalidade = 3,
                                servico = 1,
                                mensagemDeErro = "STEP 1: REQUISIÇÃO PARA ${httpUri.name} ENVIADA COM SUCESSO",
                                stackTrace = null
                        )
                )

                return success

            } catch (e: Exception) {
                logger.warn("error $e")

                logClient.createLog(
                        CreateLogRequestDto(
                                header = header,
                                aplicacao = 4,
                                classe = this::class.java.simpleName,
                                metodo = "execute",
                                parametros = "[${httpUri}]",
                                usuarioCodigo = null,
                                usuarioNome = null,
                                criticalidade = 3,
                                servico = 1,
                                mensagemDeErro = "STEP 1: ERRO AO ENVIAR REQUISIÇÃO PARA ${httpUri.name}, TENTATIVA ${retryCount + 1} DE ${restTemplateProperties.maxRetry}, ERRO: ${e.message}",
                                stackTrace = null
                        )
                )

                if (isRetryableException(e) && retryCount < restTemplateProperties.maxRetry) {

                    val httpCode = if (e is HttpClientErrorException) {
                        e.rawStatusCode
                    } else {
                        500
                    }

                    if (httpCode == 500 && httpUri.name == HttpUri.CONSULTAR_TRANSFERENCIA.name) {
                        return null
                    }

                    logRetryWarning(
                            e = e,
                            retryAttempt = retryCount + 1,
                            sleepInSeconds = restTemplateProperties.retryInterval
                    )

                    sleepInSeconds(
                            seconds = restTemplateProperties.retryInterval
                    )

                    retryCount++
                } else {
                    when {
                        isBadRequest(e) -> {
                            return null
                        }

                        isServerError(e) -> {
                            return null
                        }
                    }
                }
            }
        }
    }

    fun <Y> execute(consumer: Supplier<Y>, request: BBTransferirRequest, headerBody: BigInteger): Y? {
        var retryCount = 0
        val loteId = request.numeroRequisicao

        registrarRequest(loteId, request)

        logClient.createLog(
                CreateLogRequestDto(
                        header = headerBody,
                        aplicacao = 4,
                        classe = this::class.java.simpleName,
                        metodo = "execute",
                        parametros = "[${request}, ${headerBody}]",
                        usuarioCodigo = null,
                        usuarioNome = null,
                        criticalidade = 3,
                        servico = 1,
                        mensagemDeErro = "STEP 1: REGISTRANDO REQUEST DO LOTE $loteId",
                        stackTrace = null
                )
        )

        while (true) {
            try {
                logClient.createLog(
                        CreateLogRequestDto(
                                header = headerBody,
                                aplicacao = 4,
                                classe = this::class.java.simpleName,
                                metodo = "execute",
                                parametros = "[${request}, ${headerBody}]",
                                usuarioCodigo = null,
                                usuarioNome = null,
                                criticalidade = 3,
                                servico = 1,
                                mensagemDeErro = "STEP 1: ENVIANDO LOTE $loteId",
                                stackTrace = null
                        )
                )

                val response = consumer.get() as ResponseEntity<BBTransferenciaResponseDto>

                logClient.createLog(
                        CreateLogRequestDto(
                                header = headerBody,
                                aplicacao = 4,
                                classe = this::class.java.simpleName,
                                metodo = "execute",
                                parametros = "[${response}]",
                                usuarioCodigo = null,
                                usuarioNome = null,
                                criticalidade = 3,
                                servico = 1,
                                mensagemDeErro = "STEP 1: LOTE $loteId ENVIADO COM SUCESSO",
                                stackTrace = null
                        )
                )

                val responseJson = gson.toJson(response)

                bBLoteLogRepository.save(
                        BBLoteLogEntity(
                                lote = loteId,
                                httpCodigo = response.statusCode.value(),
                                codigoErro = null,
                                mensagemErro = null,
                                ocorrencia = null,
                                payload = responseJson
                        )
                )

                return response as Y

            } catch (e: HttpClientErrorException) {
                val erroJson = e.responseBodyAsString
                val httpStatusCode = e.statusCode.value()
                val respostaErro = gson.fromJson(erroJson, BBLiberarLoteErroResponseDto::class.java)


                logClient.createLog(CreateLogRequestDto(
                        header = headerBody,
                        aplicacao = 4,
                        classe = this::class.java.simpleName,
                        metodo = "execute",
                        parametros = "[${respostaErro}]",
                        usuarioCodigo = null,
                        usuarioNome = null,
                        criticalidade = 3,
                        servico = 1,
                        mensagemDeErro = "STEP 1: LOTE ${loteId} NÃO FOI ENVIADO: CODIGO ${httpStatusCode} - ERRO ${respostaErro.erros.map { it?.mensagem }}",
                        stackTrace = null

                ))

                bBLoteLogRepository.saveAll(
                        respostaErro
                                .erros.map { erro ->
                                    BBLoteLogEntity(
                                            lote = loteId,
                                            httpCodigo = httpStatusCode,
                                            codigoErro = erro?.codigo,
                                            mensagemErro = erro?.mensagem,
                                            ocorrencia = erro?.ocorrencia,
                                            payload = erroJson
                                    )
                                }
                )

                return null
            } catch (e: Exception) {

                if (retryCount < restTemplateProperties.maxRetry) {

                    logClient.createLog(
                            CreateLogRequestDto(
                                    header = headerBody,
                                    aplicacao = 4,
                                    classe = this::class.java.simpleName,
                                    metodo = "execute",
                                    parametros = "[${e}]",
                                    usuarioCodigo = null,
                                    usuarioNome = null,
                                    criticalidade = 3,
                                    servico = 1,
                                    mensagemDeErro = "STEP 1: TENTATIVA DE REENVIO ${retryCount + 1} DE ${restTemplateProperties.maxRetry}",
                                    stackTrace = null
                            )
                    )

                    logRetryWarning(
                            e = e,
                            retryAttempt = retryCount + 1,
                            sleepInSeconds = restTemplateProperties.retryInterval
                    )
                    sleepInSeconds(
                            seconds = restTemplateProperties.retryInterval
                    )

                    retryCount++
                } else {
                    logClient.createLog(CreateLogRequestDto(
                            header = headerBody,
                            aplicacao = 4,
                            classe = this::class.java.simpleName,
                            metodo = "execute",
                            parametros = "[${e}]",
                            usuarioCodigo = null,
                            usuarioNome = null,
                            criticalidade = 3,
                            servico = 1,
                            mensagemDeErro = "STEP 1: LOTE ${loteId} NÃO FOI ENVIADO: ERRO ${e.message} - LIMITE DE TENTATIVAS ALCANÇADO",
                            stackTrace = null
                    ))
                    return null
                }
            }
        }
    }

    fun registrarRequest(loteId: BigInteger, payload: Any) {
        val json = gson.toJson(payload)
        bBLoteLogRepository.save(
                BBLoteLogEntity(
                        lote = loteId,
                        httpCodigo = null,
                        codigoErro = null,
                        mensagemErro = null,
                        ocorrencia = null,
                        payload = json
                )
        )
    }

    private fun isRetryableException(e: java.lang.Exception): Boolean {
        return e is UnknownHostException || e is ResourceAccessException || e is HttpServerErrorException
    }

    private fun isBadRequest(e: java.lang.Exception): Boolean {
        return e is HttpClientErrorException && e.statusCode.is4xxClientError
    }

    private fun isServerError(e: java.lang.Exception): Boolean {
        return e is HttpClientErrorException && e.statusCode.is5xxServerError
    }

    private fun logRetryWarning(e: java.lang.Exception, retryAttempt: Int, sleepInSeconds: Int) {
        logger.warn(
                "PROXY --> Tentativa de reexecução {} após atraso de {} segundos. Erro: {}",
                retryAttempt,
                sleepInSeconds,
                getErrorMessage(e)
        )
    }

    private fun getErrorMessage(e: java.lang.Exception): String {
        return if (e is HttpClientErrorException) e.responseBodyAsString else e.message!!
    }

    private fun sleepInSeconds(seconds: Int) {
        try {
            Thread.sleep(seconds.toLong())
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException("Interrupção do sleep da thread", e)
        }
    }

}