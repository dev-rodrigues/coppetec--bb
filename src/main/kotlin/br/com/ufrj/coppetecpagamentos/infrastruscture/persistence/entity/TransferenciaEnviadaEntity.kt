package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.util.Objects.isNull
import java.util.stream.Collectors

data class TransferenciaEnviadaEntity(
    val id: BigInteger,
    val loteId: BigInteger? = null,
    val lancamento: BigInteger? = null,
    val estadoPagamentoId: BigInteger? = null,
    val estadoPagamento: String? = null,
    val estadoPagamentoDescricao: String? = null,
    val identificadorTransferencia: BigInteger? = null,
    val tipoCredito: Int? = null,
    val tipoCreditoDescricao: String? = null,
    val dataPagamento: String? = null,
    val valorPagamento: BigDecimal? = null,
    val documentoDebito: BigInteger? = null,
    val indicadorAceite: String? = null,
    val cnpjBeneficiario: String? = null,
    val cpfBeneficiario: String? = null,
    val agenciaCredito: String? = null,
    val contaCorrenteCredito: String? = null,
    val numeroISPB: String? = null,
    val numeroCOMPE: String? = null,
) {

    companion object {
        fun map(result: List<Array<Any>>): List<TransferenciaEnviadaEntity> {
            return if (isNull(result) || result.isEmpty()) {
                emptyList()
            } else result.stream().map { it: Array<Any> ->
                TransferenciaEnviadaEntity(
                    id = (it[0] as BigInteger),
                    loteId = (it[1] as BigInteger?),
                    lancamento = (it[2] as BigInteger?),
                    estadoPagamentoId = (it[3] as BigInteger?),
                    estadoPagamento = (it[4] as String?),
                    estadoPagamentoDescricao = (it[5] as String?),
                    identificadorTransferencia = (it[6] as BigInteger?),
                    tipoCredito = (it[7] as Int?),
                    tipoCreditoDescricao = (it[8] as String?),
                    dataPagamento = ((it[9] as Timestamp?)?.toLocalDateTime()
                        ?.format(DateTimeFormatter.ISO_DATE_TIME)!!),
                    valorPagamento = (it[10] as BigDecimal?),
                    documentoDebito = (it[11] as BigInteger?),
                    indicadorAceite = (it[12] as String?),
                    cnpjBeneficiario = (it[13] as String?),
                    cpfBeneficiario = (it[14] as String?),
                    agenciaCredito = (it[15] as String?),
                    contaCorrenteCredito = (it[16] as String?),
                    numeroISPB = (it[17] as String?),
                    numeroCOMPE = (it[18] as String?)
                )
            }.collect(Collectors.toList())
        }
    }

}