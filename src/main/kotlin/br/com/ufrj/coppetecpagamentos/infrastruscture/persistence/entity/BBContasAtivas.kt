package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.time.LocalDateTime

data class BBContasAtivas(
    val banco: String? = null,
    val agencia: String? = null,
    val agenciaSemDv: String? = null,
    val contaCorrente: String? = null,
    val contaCorrenteSemDv: String? = null,
    val consultaPeriodoDe: LocalDateTime? = null,
    val consultaPeriodoAte: LocalDateTime? = null,
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
                    consultaPeriodoDe = (it[5] as LocalDateTime?),
                    consultaPeriodoAte = (it[6] as LocalDateTime?)
                )
            }.toList()
        }
    }
}