package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.math.BigInteger

data class LogLoteEnviadoEntity(
    val idLog: BigInteger? = null,
    val idLote: BigInteger? = null,
    val codigoHttp: Int? = null,
    val codigoErro: String? = null,
    val mensagemErro: String? = null,
    val ocorrencia: String? = null,
    val payload: String? = null,
) {
    companion object {
        fun map(result: List<Array<Any>>): List<LogLoteEnviadoEntity> {
            return if (result.isEmpty()) {
                emptyList()
            } else result.stream().map { it: Array<Any> ->
                LogLoteEnviadoEntity(
                    idLog = (it[0] as BigInteger),
                    idLote = (it[1] as BigInteger),
                    codigoHttp = (it[2] as Int?),
                    codigoErro = (it[3] as String?),
                    mensagemErro = (it[4] as String?),
                    ocorrencia = (it[5] as String?),
                    payload = (it[6] as String?),
                )
            }.toList()
        }
    }
}