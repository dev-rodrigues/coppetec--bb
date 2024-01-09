package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Timestamp
import java.time.LocalDate
import java.util.Objects.isNull
import java.util.stream.Collectors

data class TransferenciaPendenteDatabase(
    var banco: String? = null,
    var agenciaDebito: String? = null,
    var agenciaDebitoDigito: String? = null,
    var contaDebito: String? = null,
    var contaDebitoDigito: String? = null,
    var contaFonte: String? = null,
    var tipoPagamento: String? = null,
    var numeroCOMPE: String? = null,
    var numeroISPB: String? = null,
    var beneficiarioBanco: String? = null,
    var beneficiarioAgencia: BigInteger? = null,
    var beneficiarioContaCorrente: BigInteger? = null,
    var beneficiarioContaCorrenteDV: String? = null,
    var beneficiarioCPF: String? = null,
    var beneficiarioCNPJ: String? = null,
    var transferenciaData: LocalDate? = null,
    var transferenciaValor: BigDecimal? = null,
    var documentoDebito: BigInteger? = null,
    var documentoCredito: BigInteger? = null,
    var codigoFinalidadeDOC: String? = null,
    var codigoFinalidadeTED: String? = null,
    var numeroDepositoJudicial: String? = null,
    var descricaoTransferencia: String? = null,
) {
    companion object {
        fun map(result: List<Array<Any>>): List<TransferenciaPendenteDatabase> {
            return if (isNull(result) || result.isEmpty()) {
                emptyList()
            } else result
                .stream()
                .map { it: Array<Any> ->
                    TransferenciaPendenteDatabase(
                        banco = (it[0] as String?),
                        agenciaDebito = (it[1] as String?),
                        agenciaDebitoDigito = ((it[2] as Char?).toString()),
                        contaDebito = (it[3] as String?),
                        contaDebitoDigito = ((it[4] as Char?).toString()),
                        contaFonte = (it[5] as String?),
                        tipoPagamento = (it[6] as String?),
                        numeroCOMPE = (it[7] as String?),
                        numeroISPB = (it[8] as String?),
                        beneficiarioBanco = (it[9] as String?),
                        beneficiarioAgencia = (it[10] as BigInteger?),
                        beneficiarioContaCorrente = (it[11] as BigInteger?),
                        beneficiarioContaCorrenteDV = ((it[12] as Char?).toString()),
                        beneficiarioCPF = (it[13] as String?),
                        beneficiarioCNPJ = (it[14] as String?),
                        transferenciaData = ((it[15] as Timestamp?)?.toLocalDateTime()?.toLocalDate())!!,
                        transferenciaValor = (it[16] as BigDecimal?),
                        documentoDebito = (it[17] as BigInteger?),
                        documentoCredito = (it[18] as BigInteger?),
                        codigoFinalidadeDOC = (it[19] as String?),
                        codigoFinalidadeTED = (it[20] as String?),
                        numeroDepositoJudicial = (it[21] as String?),
                        descricaoTransferencia = (it[22] as String?)
                    )
                }.collect(Collectors.toList())
        }
    }

}