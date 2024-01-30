package br.com.ufrj.coppetecpagamentos.infrastruscture.http.middleware

import br.com.ufrj.coppetecpagamentos.domain.adapter.LocalDateTimeAdapter
import br.com.ufrj.coppetecpagamentos.domain.model.HttpUri
import br.com.ufrj.coppetecpagamentos.domain.property.RestTemplateProperties
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
    private val meterRegistry: MeterRegistry
) {

    private val logger = LoggerFactory.getLogger(RestTemplateProxy::class.java)
    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    fun <Y> execute(consumer: Supplier<Y>, httpUri: HttpUri): Y? {
        var retryCount = 0
        while (true) {
            try {
                val success = consumer.get()

                Counter
                    .builder("http_${httpUri.name}_request_success_total")
                    .tags("http_status", "200")
                    .description("Total number of successful HTTP requests")
                    .register(meterRegistry).increment()

                return success
            } catch (e: Exception) {
                logger.warn("error $e")

                if (isRetryableException(e) && retryCount < restTemplateProperties.maxRetry) {

                    val httpCode = if (e is HttpClientErrorException) {
                        e.rawStatusCode
                    } else {
                        500
                    }

                    Counter
                        .builder("http_${httpUri.name}_request_retry_total")
                        .tags("http_status", httpCode.toString())
                        .description("Total number of retried HTTP requests")
                        .register(meterRegistry)

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
                            Counter.builder("http_${httpUri.name}_request_error_total")
                                .tags("http_status", "400")
                                .description("Total number of HTTP requests with errors")
                                .register(meterRegistry)
                            return null
                        }

                        isServerError(e) -> {
                            Counter.builder("http_${httpUri.name}_request_error_total")
                                .tags("http_status", "500")
                                .description("Total number of HTTP requests with errors")
                                .register(meterRegistry)

                            return null
                        }
                    }
                }
            }
        }
    }

    fun <Y> execute(consumer: Supplier<Y>, request: BBTransferirRequest): Y? {
        var retryCount = 0
        val loteId = request.numeroRequisicao

        registrarRequest(loteId, request)

        while (true) {
            try {
                val response = consumer.get() as ResponseEntity<BBTransferenciaResponseDto>
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
        return e is UnknownHostException || e is ResourceAccessException || e is HttpServerErrorException // e is HttpClientErrorException ||
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

    @Throws(java.lang.RuntimeException::class)
    private fun logErrorAndThrowException(e: java.lang.Exception) {
        logger.error("PROXY -->  Erro: {}", getErrorMessage(e))

        throw RuntimeException(
            "PROXY --> Erro: " + getErrorMessage(
                e
            ), e
        )
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