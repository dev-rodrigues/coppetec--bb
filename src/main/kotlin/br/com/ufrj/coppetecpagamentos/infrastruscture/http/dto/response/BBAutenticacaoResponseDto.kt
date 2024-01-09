package br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class BBAutenticacaoResponseDto(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("expires_in")
    val expiresIn: Long,

    @JsonProperty("creationDate")
    val creationDate: LocalDateTime? = null
)