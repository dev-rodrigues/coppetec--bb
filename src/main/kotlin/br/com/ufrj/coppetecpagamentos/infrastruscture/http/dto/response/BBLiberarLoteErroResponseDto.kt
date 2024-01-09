package br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class BBLiberarLoteErroResponseDto(
    @JsonProperty("erros") var erros: List<Erro?>
)

data class Erro(
    @JsonProperty("codigo") var codigo: String? = null,
    @JsonProperty("versao") val versao: String? = null,
    @JsonProperty("mensagem") val mensagem: String? = null,
    @JsonProperty("ocorrencia") val ocorrencia: String? = null
)
