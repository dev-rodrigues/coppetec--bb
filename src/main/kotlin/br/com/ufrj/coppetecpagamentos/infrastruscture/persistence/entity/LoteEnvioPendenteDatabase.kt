package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.util.Objects.isNull
import java.util.stream.Collectors

class LoteEnvioPendenteDatabase {

    var banco: String? = null
    var agenciaDebito: String? = null
    var digitoAgenciaDebito: String? = null
    var contaDebito: String? = null
    var contaDebitoDigito: String? = null
    var contaOrigem: String? = null
    var tipoPagamento: String? = null
    var contratoDePagamento: String? = null
    var compe: String? = null
    var ispb: String? = null

    constructor() {
        // Default constructor
    }

    constructor(
        banco: String,
        agenciaDebito: String,
        digitoAgenciaDebito: String,
        contaDebito: String,
        contaDebitoDigito: String,
        contaOrigem: String,
        tipoPagamento: String,
        contratoDePagamento: String,
        compe: String,
        ispb: String
    ) {
        this.banco = banco
        this.agenciaDebito = agenciaDebito
        this.digitoAgenciaDebito = digitoAgenciaDebito
        this.contaDebito = contaDebito
        this.contaDebitoDigito = contaDebitoDigito
        this.contaOrigem = contaOrigem
        this.tipoPagamento = tipoPagamento
        this.contratoDePagamento = contratoDePagamento
        this.compe = compe
        this.ispb = ispb
    }

    companion object {
        fun map(result: List<Array<Any>>): List<LoteEnvioPendenteDatabase> {
            return if (isNull(result) || result.isEmpty()) {
                emptyList()
            } else result
                .stream()
                .map { it: Array<Any> ->
                    LoteEnvioPendenteDatabase(
                        (it[0] as String?)!!,
                        (it[1] as String?)!!,
                        ((it[2] as Char?).toString()),
                        (it[3] as String?)!!,
                        ((it[4] as Char?).toString()),
                        (it[5] as String?)!!,
                        (it[6] as String?)!!,
                        (it[7] as String?)!!,
                        (it[8] as String?)!!,
                        (it[9] as String?)!!
                    )
                }.collect(Collectors.toList())
        }
    }
}