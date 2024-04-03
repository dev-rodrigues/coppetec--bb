package br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.impl

import br.com.ufrj.coppetecpagamentos.domain.exception.BadRequestExtratoException
import br.com.ufrj.coppetecpagamentos.domain.model.API
import br.com.ufrj.coppetecpagamentos.domain.model.CreateLogRequestDto
import br.com.ufrj.coppetecpagamentos.domain.model.HttpUri
import br.com.ufrj.coppetecpagamentos.domain.model.HttpUri.*
import br.com.ufrj.coppetecpagamentos.domain.model.TipoHeader
import br.com.ufrj.coppetecpagamentos.domain.model.TipoHeader.AUTENTICACAO
import br.com.ufrj.coppetecpagamentos.domain.model.TipoHeader.CONSULTA_AUTENTICADO
import br.com.ufrj.coppetecpagamentos.domain.property.BBProperties
import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
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

@Suppress("UNREACHABLE_CODE")
@Component
class BBPortImpl(
        private val bBProperties: BBProperties,
        private val client: RestTemplate,
        private val proxy: RestTemplateProxy,
        private val mapper: ObjectMapper,
        private val logClient: LogClient,
) : BBPort {

    private val logger = LoggerFactory.getLogger(BBPortImpl::class.java)

    override fun autenticar(
            api: API,
            header: BigInteger,
    ): ResponseEntity<BBAutenticacaoResponseDto> {
        val response = proxy.execute(
                {
                    client.exchange(
                            getURI(AUTENTICAR, emptyList(), emptyMap(), api),
                            POST,
                            getParameter(api),
                            BBAutenticacaoResponseDto::class.java
                    )
                }, AUTENTICAR, header
        )

        return if (nonNull(response)) {
            response!!
        } else {
            logger.error("Erro ao autenticar")
            logClient.createLog(
                    CreateLogRequestDto(
                            header = header,
                            aplicacao = 1,
                            classe = this::class.java.simpleName,
                            metodo = "autenticar",
                            parametros = "[${AUTENTICAR}, ${getParameter(api)}]",
                            usuarioCodigo = null,
                            usuarioNome = null,
                            criticalidade = 1,
                            servico = 1,
                            mensagemDeErro = "Erro ao autenticar",
                            stackTrace = null
                    )
            )

            throw RuntimeException("Erro ao autenticar")
        }
    }

    override fun consultarLote(idLote: BigInteger, accessToken: String, header: BigInteger): ResponseEntity<BBConsultaLoteResponseDto>? {
        val response = proxy.execute(
                {
                    client.exchange(
                            getURI(CONSULTAR_LOTE, listOf(idLote), emptyMap(), API.TRANSFERENCIA),
                            GET,
                            getParameter(accessToken),
                            BBConsultaLoteResponseDto::class.java
                    )
                }, CONSULTAR_LOTE, header
        )

        return if (nonNull(response)) {
            response!!
        } else null
    }

    override fun consultarTransferencia(
            identificadorTransferencia: BigInteger, accessToken: String, header: BigInteger,
    ): BBConsultaTransferenciaResponseDto? {
        val response = proxy.execute(
                {
                    client.exchange(
                            getURI(CONSULTAR_TRANSFERENCIA, listOf(identificadorTransferencia), emptyMap(), API.TRANSFERENCIA),
                            GET,
                            getParameter(accessToken),
                            String::class.java
                    )
                }, CONSULTAR_TRANSFERENCIA, header
        )

        return if (nonNull(response)) {
            return formatBBConsultaTransferenciaResponseDto(response!!)
        } else {
            logClient.createLog(
                    CreateLogRequestDto(
                            header = header,
                            aplicacao = 1,
                            classe = this::class.java.simpleName,
                            metodo = "consultarTransferencia",
                            parametros = "[${identificadorTransferencia}, $accessToken]",
                            usuarioCodigo = null,
                            usuarioNome = null,
                            criticalidade = 1,
                            servico = 1,
                            mensagemDeErro = "Erro ao consultar transferencia $identificadorTransferencia no banco do Brasil",
                            stackTrace = null
                    )
            )
            logger.error("Erro ao consultar transferencia")
            return null
        }
    }

    override fun consultarExtrato(
            numeroPaginaSolicitacao: Int?,
            agencia: String,
            conta: String,
            token: String,
            dataInicioSolicitacao: String,
            dataFimSolicitacao: String,
            headerBody: BigInteger,
    ): ResponseEntity<BBConsultaExtratoResponseDto> {
        val response = proxy.execute(
                {
                    client.exchange(
                            getURI(
                                    CONSULTAR_EXTRATO, listOf(), mapOf(
                                    "agencia" to agencia,
                                    "conta" to conta,
                                    "dataInicioSolicitacao" to dataInicioSolicitacao,
                                    "dataFimSolicitacao" to dataFimSolicitacao,
                                    "numeroPaginaSolicitacao" to numeroPaginaSolicitacao.toString(),
                            ), API.EXTRATO
                            ),
                            GET,
                            getParameter(token),
                            BBConsultaExtratoResponseDto::class.java
                    )
                }, CONSULTAR_EXTRATO, headerBody
        )

        return if (nonNull(response)) {
            response!!
        } else {
            throw BadRequestExtratoException(
                    message = "Erro ao consultar extrato",
                    ag = agencia,
                    cc = conta,
                    de = dataInicioSolicitacao,
                    ate = dataFimSolicitacao
            )
        }
    }

    override fun transferir(
            loteDeEnvio: BBTransferirRequest, token: String, headerBody: BigInteger,
    ): ResponseEntity<BBTransferenciaResponseDto>? {
        return proxy.execute(
                {
                    client.exchange(
                            getURI(ENVIAR_LOTE, emptyList(), emptyMap(), API.TRANSFERENCIA),
                            POST,
                            getParameter(loteDeEnvio, token),
                            BBTransferenciaResponseDto::class.java
                    )
                }, loteDeEnvio, headerBody
        )
    }

    private fun formatBBConsultaTransferenciaResponseDto(
            response: ResponseEntity<String>,
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

    private fun getURI(typeURI: HttpUri, parameters: List<Any>, maps: Map<String, String>, api: API): String {
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
                val url = buildUri(endpoint, maps, api)
                logger.info("URL DE CONSULTA DE TRANSFERENCIAS: {}", url)
                return url
            }

            CONSULTAR_EXTRATO -> {
                endpoint = bBProperties.endpoints.extrato
                logMessage = "URL DE CONSULTA DE TRANSFERENCIAS: {}"
                logger.info(logMessage, endpoint)

                val agencia = maps["agencia"]
                val conta = maps["conta"]
                val numeroPaginaSolicitacao = maps["numeroPaginaSolicitacao"]
                val dataInicioSolicitacao = maps["dataInicioSolicitacao"]
                val dataFimSolicitacao = maps["dataFimSolicitacao"]

                val uriAux = UriComponentsBuilder.fromHttpUrl(endpoint)
                        .path("/extratos/v1/conta-corrente/agencia/{agencia}/conta/{conta}")
                        .buildAndExpand(mapOf("agencia" to agencia, "conta" to conta)).toUriString()

                val url = buildUri(
                        endpoint = uriAux, parameters = if (numeroPaginaSolicitacao != null) {
                    mapOf(
                            Pair("numeroPaginaSolicitacao", numeroPaginaSolicitacao),
                            Pair("dataInicioSolicitacao", dataInicioSolicitacao!!),
                            Pair("dataFimSolicitacao", dataFimSolicitacao!!)
                    )
                } else emptyMap(), api = api
                )
                logger.info("URL DE CONSULTA DO EXTRATO: {}", url)
                return url
            }

            ENVIAR_LOTE -> {
                endpoint = bBProperties.endpoints.transferir
                logMessage = "URL DE ENVIO DO LOTE: {}"
            }

            LIBERAR_LOTE -> {
                endpoint = bBProperties.endpoints.liberarLote
                val url = buildUri(endpoint, maps, api)
                logger.info("URL DE LIBERACAO DE LOTE: {}", url)
                return url
            }

        }

        val uri = UriComponentsBuilder.fromUriString(endpoint)
                .queryParam(bBProperties.ambiente, bBProperties.autenticacaoTransferencia.chaveAplicacao)

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

    private fun buildUri(endpoint: String, parameters: Map<String, String>, api: API): String {
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()

        when (api) {
            API.TRANSFERENCIA -> params.add(
                    bBProperties.ambiente, bBProperties.autenticacaoTransferencia.chaveAplicacao
            )

            API.EXTRATO -> params.add(bBProperties.ambiente, bBProperties.autenticacaoExtrato.chaveAplicacao)
        }

        if (nonNull(parameters) && parameters.isNotEmpty()) {
            for ((key, value) in parameters) {
                params.add(key, value)
            }
        }
        val builder = UriComponentsBuilder.fromUriString(endpoint).queryParams(params)

        return builder.buildAndExpand().toString()
    }

    private fun getParameter(api: API = API.TRANSFERENCIA): HttpEntity<MultiValueMap<String, String>?> {
        return HttpEntity<MultiValueMap<String, String>?>(
                null, getHeader(
                typeHeader = AUTENTICACAO, token = null, api = api
        )
        )
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
                Gson().toJson(body), getHeader(
                CONSULTA_AUTENTICADO, token
        )
        )
    }

    private fun getHeader(
            typeHeader: TipoHeader, token: String?, api: API = API.TRANSFERENCIA,
    ): HttpHeaders {
        val header = HttpHeaders()
        if (typeHeader == AUTENTICACAO) {
            header.add("Content-Type", "application/x-www-form-urlencoded")

            when (api) {
                API.TRANSFERENCIA -> header.add("Authorization", bBProperties.autenticacaoTransferencia.autorizacao)
                API.EXTRATO -> header.add("Authorization", bBProperties.autenticacaoExtrato.autorizacao)
            }
        } else if (typeHeader == CONSULTA_AUTENTICADO) {
            header.contentType = MediaType.APPLICATION_JSON
            header.add("Authorization", "Bearer $token")
        }
        return header
    }
}