package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.math.BigInteger

data class ErroTransferenciaEntity(
    val id: BigInteger,
    val transferenciaId: BigInteger,
    val codigoErro: Int,
    val descricaoErro: String
) {
    companion object {
        fun map(result: List<Array<Any>>): List<ErroTransferenciaEntity> {
            return if (result.isEmpty()) {
                emptyList()
            } else result.stream().map { it: Array<Any> ->
                ErroTransferenciaEntity(
                    id = (it[0] as BigInteger),
                    transferenciaId = (it[1] as BigInteger),
                    codigoErro = (it[2] as Int),
                    descricaoErro = (it[3] as String)
                )
            }.toList()
        }
    }
}