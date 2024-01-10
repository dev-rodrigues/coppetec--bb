package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.util.Objects.isNull
import java.util.stream.Collectors.toList

data class LoteEnviadoEntity(
    val id: BigInteger,
    val dataHora: String,
    val estadoRequisicao: Int? = null,
    val descricaoEstadoRequisicao: String? = null,
    val quantidadesTransferencias: Int? = 0,
    val valorTransferencias: BigDecimal? = BigDecimal.ZERO,
    val quantidadeTransferenciasValidas: Int? = 0,
    val valorTransferenciasValidas: BigDecimal? = BigDecimal.ZERO,
) {

    companion object {
        fun map(result: List<Array<Any>>): List<LoteEnviadoEntity> {
            return if (isNull(result) || result.isEmpty()) {
                emptyList()
            } else result.stream().map { it: Array<Any> ->
                LoteEnviadoEntity(
                    id = (it[0] as BigInteger),
                    dataHora = ((it[1] as Timestamp?)?.toLocalDateTime()?.format(DateTimeFormatter.ISO_DATE_TIME)!!),
                    estadoRequisicao = (it[2] as Int?),
                    descricaoEstadoRequisicao = (it[3] as String?),
                    quantidadesTransferencias = (it[4] as Int?),
                    valorTransferencias = (it[5] as Double?)?.toBigDecimal(),
                    quantidadeTransferenciasValidas = (it[6] as Int?),
                    valorTransferenciasValidas = (it[7] as Double?)?.toBigDecimal()
                )
            }.collect(toList())
        }
    }
}