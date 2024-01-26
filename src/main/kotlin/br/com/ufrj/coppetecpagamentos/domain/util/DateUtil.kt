package br.com.ufrj.coppetecpagamentos.domain.util

import java.math.BigInteger
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtil {
    val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun formatDate(bbDate: Long): LocalDateTime? {
        if (bbDate == BigInteger.ZERO.toLong()) {
            return null
        }

        // Extrair dia, mês e ano do valor Long
        val ano = (bbDate % 10000).toInt()
        val mes = ((bbDate / 10000) % 100).toInt()
        val dia = (bbDate / 1000000).toInt()

        return LocalDateTime.of(ano, mes, dia, 0, 0)
    }
}
