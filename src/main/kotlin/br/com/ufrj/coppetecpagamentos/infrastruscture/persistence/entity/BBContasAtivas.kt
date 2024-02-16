package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BBContasAtivas(
    val banco: String? = null,
    val agencia: String? = null,
    val agenciaSemDv: String? = null,
    val contaCorrente: String? = null,
    val contaCorrenteSemDv: String? = null,
    val consultaPeriodoDe: String? = null,
    val consultaPeriodoAte: String? = null,
) {
    companion object {
        fun map(result: List<Array<Any>>): List<BBContasAtivas> {

            return if (result.isEmpty()) {
                emptyList()
            } else result.stream().map { it: Array<Any> ->
                BBContasAtivas(
                    banco = (it[0] as String?),
                    agencia = (it[1] as String?),
                    agenciaSemDv = (it[2] as String?),
                    contaCorrente = (it[3] as String?),
                    contaCorrenteSemDv = (it[4] as String?),
                    consultaPeriodoDe = formatAndRemoveLeadingZero((it[5] as Timestamp?)?.toLocalDateTime()),
                    consultaPeriodoAte = formatAndRemoveLeadingZero((it[6] as Timestamp?)?.toLocalDateTime())

                )
            }.toList()
        }

        private fun formatAndRemoveLeadingZero(date: LocalDateTime?): String {
            val formatter = DateTimeFormatter.ofPattern("ddMMyyyy")
            val formattedDate = date?.format(formatter) ?: ""
            return if (formattedDate.startsWith("0")) formattedDate.substring(1) else formattedDate
        }
    }
}