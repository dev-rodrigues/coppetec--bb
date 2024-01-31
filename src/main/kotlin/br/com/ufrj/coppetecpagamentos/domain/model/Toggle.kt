package br.com.ufrj.coppetecpagamentos.domain.model

object Toggle {
    val BB_TRANSFERENCIA_STP1_SCHEDULE = 1
    val BB_TRANSFERENCIA_STP2_SCHEDULE = 2
    val BB_TRANSFERENCIA_STP3_SCHEDULE = 3
}

data class ToggleModel(
    val name: String,
    val state: Boolean
)