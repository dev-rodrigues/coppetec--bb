package br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.request

import java.math.BigInteger

data class BBLiberacaoLoteRequest(
    val numeroRequisicao: BigInteger,
    val indicadorFloat: String
)