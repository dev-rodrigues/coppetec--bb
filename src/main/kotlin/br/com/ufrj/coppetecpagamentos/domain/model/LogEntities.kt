package br.com.ufrj.coppetecpagamentos.domain.model

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

data class LogHeaderDto(
        @SerializedName("id") val id: BigInteger,
        @SerializedName("dataHora") val dataHora: String,
)

data class CreateLogRequestDto(
        val header: Number,
        val aplicacao: Int,
        val classe: String,
        val metodo: String,
        val parametros: String,
        val usuarioCodigo: String? = null,
        val usuarioNome: String? = null,
        val criticalidade: Int,
        val servico: Int,
        val mensagemDeErro: String,
        val stackTrace: String? = null,
)

data class CreateLogResponseDto(
        val id: BigInteger,
)