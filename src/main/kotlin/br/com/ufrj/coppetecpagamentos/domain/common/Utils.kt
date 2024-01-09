package br.com.ufrj.coppetecpagamentos.domain.common

import java.math.BigInteger
import java.time.LocalDateTime

fun formatarData(dataResponse: BigInteger): LocalDateTime {
    val ano = dataResponse.intValueExact() % 10000
    val mes = dataResponse.intValueExact() % 1000000 / 10000
    val dia = dataResponse.intValueExact() / 1000000

    return LocalDateTime.of(ano, mes, dia, 0, 0, 0)
}