package br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class BBLoteResponseError(
    @JsonProperty("erros")
    val erros: List<BBLoteErroResponse>? = null
)

data class BBLoteErroResponse(
    @JsonProperty("codigo")
    val codigo: String? = null,

    @JsonProperty("versao")
    val versao: String? = null,

    @JsonProperty("mensagem")
    val mensagem: String? = null,

    @JsonProperty("ocorrencia")
    val ocorrencia: String? = null
)

