package br.com.ufrj.coppetecpagamentos.domain.util

data object ContaUtil {
    fun getDV(agencia: String): String {
        val agenciaReversed = agencia.reversed()
        val agenciaSplits = agenciaReversed.split("")
        return agenciaSplits[1]
    }

    fun getAgenciaFull(agencia: String): String {
        val sanitized = agencia
            .replace("-", "")
            .replace(".", "")

        return "0$sanitized"
    }

    fun getCC(cc: String): String {
        val sanitized = cc
            .replace("-", "")
            .replace(".", "")

        return sanitized.padStart(12, '0').take(12)
    }
}