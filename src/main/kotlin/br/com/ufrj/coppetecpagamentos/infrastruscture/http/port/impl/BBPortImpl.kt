package br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.impl

import br.com.ufrj.coppetecpagamentos.domain.model.HttpUri
import br.com.ufrj.coppetecpagamentos.domain.model.HttpUri.*
import br.com.ufrj.coppetecpagamentos.domain.model.TipoHeader
import br.com.ufrj.coppetecpagamentos.domain.model.TipoHeader.AUTENTICACAO
import br.com.ufrj.coppetecpagamentos.domain.model.TipoHeader.CONSULTA_AUTENTICADO
import br.com.ufrj.coppetecpagamentos.domain.property.BBProperties
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.request.BBLiberacaoLoteRequest
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.request.BBTransferirRequest
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.*
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.middleware.RestTemplateProxy
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.lang.NonNull
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.math.BigInteger
import java.util.Objects.nonNull
import java.util.regex.Pattern

@Component
class BBPortImpl(
    private val bBProperties: BBProperties,
    private val client: RestTemplate,
    private val proxy: RestTemplateProxy,
    private val mapper: ObjectMapper
) : BBPort {

    private val logger = LoggerFactory.getLogger(BBPortImpl::class.java)

    override fun autenticar(): ResponseEntity<BBAutenticacaoResponseDto> {
        return proxy.execute {
            client.exchange(
                getURI(AUTENTICAR, emptyList(), emptyMap()),
                POST,
                getParameter(),
                BBAutenticacaoResponseDto::class.java
            )
        }
    }

    override fun liberarLote(body: BBLiberacaoLoteRequest, token: String): ResponseEntity<BBLiberacaoLoteResponse> {
        return proxy.execute {
            client.exchange(
                getURI(ENVIAR_LOTE, emptyList(), emptyMap()),
                POST,
                getParameter(body, token),
                BBLiberacaoLoteResponse::class.java
            )
        }
    }

    override fun consultarLote(idLote: BigInteger, accessToken: String): ResponseEntity<BBConsultaLoteResponseDto> {
        return proxy.execute {
            client.exchange(
                getURI(CONSULTAR_LOTE, listOf(idLote), emptyMap()),
                GET,
                getParameter(accessToken),
                BBConsultaLoteResponseDto::class.java
            )
        }
    }

    override fun consultarTransferencia(
        identificadorTransferencia: BigInteger, accessToken: String
    ): BBConsultaTransferenciaResponseDto {
        val response = proxy.execute {
            client.exchange(
                getURI(CONSULTAR_TRANSFERENCIA, listOf(identificadorTransferencia), emptyMap()),
                GET,
                getParameter(accessToken),
                String::class.java
            )
        }

        return formatBBConsultaTransferenciaResponseDto(response)
    }

    override fun consultarExtrato(agencia: String, conta: String, token: String): String {
        val response = proxy.execute {
            client.exchange(
                getURI(CONSULTAR_EXTRATO, listOf(), mapOf("agencia" to agencia, "conta" to conta)),
                GET,
                getParameter(token),
                String::class.java
            )
        }

        return response.body!!
    }

    override fun transferir(
        loteDeEnvio: BBTransferirRequest,
        token: String
    ): ResponseEntity<BBTransferenciaResponseDto>? {
        return proxy.execute(
            {
                client.exchange(
                    getURI(ENVIAR_LOTE, emptyList(), emptyMap()),
                    POST,
                    getParameter(loteDeEnvio, token),
                    BBTransferenciaResponseDto::class.java
                )
            },
            loteDeEnvio
        )
    }

    private fun formatBBConsultaTransferenciaResponseDto(
        response: ResponseEntity<String>
    ): BBConsultaTransferenciaResponseDto {
        val json = response.body

        try {
            val regex = "(,\\s*\"texto\":\\s*\"[^\"]*\")"
            val pattern = Pattern.compile(regex)
            val matcher = json?.let { pattern.matcher(it) }
            val jsonStringSemTexto = matcher?.replaceAll("")

            return mapper.readValue(jsonStringSemTexto, BBConsultaTransferenciaResponseDto::class.java)
        } catch (e: JsonProcessingException) {
            throw RuntimeException("Erro ao formatar resposta", e)
        }
    }

    private fun getURI(typeURI: HttpUri, parameters: List<Any>, maps: Map<String, String>): String {
        val endpoint: String
        val logMessage: String

        when (typeURI) {
            AUTENTICAR -> {
                endpoint = bBProperties.endpoints.autenticar
                logMessage = "URL DE AUTENTICACAO: {}"
                logger.info(logMessage, endpoint)
                val url = buildAuthUri(endpoint)
                logger.info("URL DE AUTENTICACAO: {}", url)
                return url
            }

            CONSULTAR_LOTE -> {
                endpoint = bBProperties.endpoints.buscarSolicitacao
                logMessage = "URL DE CONSULTA LOTE: {}"
                logger.info(logMessage, endpoint)
            }

            CONSULTAR_TRANSFERENCIA -> {
                endpoint = bBProperties.endpoints.buscarTransferencia
                logMessage = "URL DE ENVIO DO LOTE: {}"
            }

            CONSULTAR_TRANSFERENCIAS -> {
                endpoint = bBProperties.endpoints.consultar
                logMessage = "URL DE CONSULTA DE TRANSFERENCIAS: "
                logger.info(logMessage, endpoint)
                val url = buildUri(endpoint, maps)
                logger.info("URL DE CONSULTA DE TRANSFERENCIAS: {}", url)
                return url
            }

            CONSULTAR_EXTRATO -> {
                endpoint = bBProperties.endpoints.extrato
                logMessage = "URL DE CONSULTA DE TRANSFERENCIAS: "
                logger.info(logMessage, endpoint)
                val url = buildUri(endpoint, maps)
                logger.info("URL DE CONSULTA DO EXTRATO: {}", url)
                return url
            }

            ENVIAR_LOTE -> {
                endpoint = bBProperties.endpoints.transferir
                logMessage = "URL DE ENVIO DO LOTE: {}"
            }

            LIBERAR_LOTE -> {
                endpoint = bBProperties.endpoints.liberarLote
                val url = buildUri(endpoint, maps)
                logger.info("URL DE LIBERACAO DE LOTE: {}", url)
                return url
            }

        }

        val uri = UriComponentsBuilder.fromUriString(endpoint)
            .queryParam(bBProperties.autenticacao.ambiente, bBProperties.autenticacao.chaveAplicacao)

        if (nonNull(parameters) && parameters.isNotEmpty()) {
            val formated = uri.buildAndExpand(parameters[0].toString()).toString()
            val urlSemColchetes = formated.replace("[", "").replace("]", "")

            logger.info(logMessage, urlSemColchetes)

            return urlSemColchetes
        }

        return uri.buildAndExpand().toString()
    }

    private fun buildAuthUri(endpoint: String): String {
        return UriComponentsBuilder.fromUriString(endpoint).queryParam("grant_type", "client_credentials").toUriString()
    }

    private fun buildUri(endpoint: String, parameters: Map<String, String>): String {
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()

        params.add(bBProperties.autenticacao.ambiente, bBProperties.autenticacao.chaveAplicacao)

        if (nonNull(parameters) && parameters.isNotEmpty()) {
            for ((key, value) in parameters) {
                params.add(key, value)
            }
        }
        val builder = UriComponentsBuilder.fromUriString(endpoint).queryParams(params)

        return builder.buildAndExpand().toString()
    }

    private fun getParameter(): HttpEntity<MultiValueMap<String, String>?> {
        return HttpEntity<MultiValueMap<String, String>?>(null, getHeader(AUTENTICACAO, null))
    }

    private fun getParameter(token: String): HttpEntity<MultiValueMap<String, String>?> {
        return HttpEntity<MultiValueMap<String, String>?>(
            null, getHeader(
                CONSULTA_AUTENTICADO, token
            )
        )
    }

    private fun getParameter(@NonNull body: Any, @NonNull token: String): HttpEntity<Any> {
        return HttpEntity<Any>(
            Gson().toJson(body),
            getHeader(
                CONSULTA_AUTENTICADO, token
            )
        )
    }

    private fun getHeader(
        typeHeader: TipoHeader, token: String?
    ): HttpHeaders {
        val header = HttpHeaders()
        if (typeHeader == AUTENTICACAO) {
            header.add("Content-Type", "application/x-www-form-urlencoded")
            header.add("Authorization", bBProperties.autenticacao.autorizacao)
        } else if (typeHeader == CONSULTA_AUTENTICADO) {
            header.contentType = MediaType.APPLICATION_JSON
            header.add("Authorization", "Bearer $token")
        }
        return header
    }
}