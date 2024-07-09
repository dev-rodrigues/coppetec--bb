package br.com.ufrj.coppetecpagamentos.domain.util

import java.math.BigInteger
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

    fun formatDate(dbDate: String): LocalDateTime {
        val formattedDateString = if (dbDate.length == 7) {
            "0$dbDate"
        } else {
            dbDate
        }

        val formatter = DateTimeFormatter.ofPattern("ddMMyyyy")
        val localDate = LocalDate.parse(formattedDateString, formatter)
        val localTime = LocalTime.MIDNIGHT
        return LocalDateTime.of(localDate, localTime)
    }
}