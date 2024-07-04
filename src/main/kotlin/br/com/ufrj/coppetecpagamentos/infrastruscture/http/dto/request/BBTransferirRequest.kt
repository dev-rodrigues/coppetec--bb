package br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.request

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.TransferenciaPendenteDatabase
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBLoteEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LoteEnvioPendenteDatabase
import java.math.BigDecimal
import java.math.BigInteger
import java.time.format.DateTimeFormatter

data class BBTransferirRequest(
    var numeroRequisicao: BigInteger,
    var numeroContratoPagamento: String,
    var agenciaDebito: BigInteger,
    var contaCorrenteDebito: BigInteger,
    var digitoVerificadorContaCorrente: BigInteger,
    var tipoPagamento: Int,
    var listaTransferencias: List<BBTransferenciaRequest> = mutableListOf()
) {
    companion object {
        fun mapLoteRequest(
            loteEnvioPendenteDatabase: LoteEnvioPendenteDatabase,
            lote: BBLoteEntity
        ): BBTransferirRequest {
            return BBTransferirRequest(
                lote.id!!,
                loteEnvioPendenteDatabase.contratoDePagamento!!,
                loteEnvioPendenteDatabase.agenciaDebito?.toBigInteger()!!,
                loteEnvioPendenteDatabase.contaDebito?.toBigInteger()!!,
                loteEnvioPendenteDatabase.contaDebitoDigito?.toBigInteger()!!,
                loteEnvioPendenteDatabase.tipoPagamento?.toInt()!!,
                mutableListOf()
            )
        }
    }
}

data class BBTransferenciaRequest(
    var numeroCOMPE: BigInteger? = null,
    var numeroISPB: BigInteger? = null,
    var agenciaCredito: BigInteger? = null,
    var contaCorrenteCredito: BigInteger? = null,
    var digitoVerificadorContaCorrente: String? = null,
    var contaPagamentoCredito: String? = null,
    var cpfBeneficiario: Long? = null,
    var cnpjBeneficiario: Long? = null,
    var dataTransferencia: BigInteger? = null,
    var valorTransferencia: BigDecimal? = null,
    var documentoDebito: BigInteger? = null,
    var documentoCredito: BigInteger? = null,
    var codigoFinalidadeDOC: BigInteger? = null,
    var codigoFinalidadeTED: BigInteger? = null,
    var numeroDepositoJudicial: String? = null,
    var descricaoTransferencia: String? = null,
) {
    companion object {
        fun mapTransferenciaRequest(transacaoPendente: TransferenciaPendenteDatabase): BBTransferenciaRequest {
            return BBTransferenciaRequest(
                numeroCOMPE = transacaoPendente.numeroCOMPE?.toBigInteger(),
                numeroISPB = transacaoPendente.numeroISPB?.toBigInteger(),
                agenciaCredito = transacaoPendente.beneficiarioAgencia,
                contaCorrenteCredito = transacaoPendente.beneficiarioContaCorrente,
                digitoVerificadorContaCorrente = transacaoPendente.beneficiarioContaCorrenteDV,
                contaPagamentoCredito = null,
                cpfBeneficiario = transacaoPendente.beneficiarioCPF?.toLong(),
                cnpjBeneficiario = transacaoPendente.beneficiarioCNPJ?.toLong(),
                dataTransferencia = transacaoPendente.transferenciaData?.format(DateTimeFormatter.ofPattern("ddMMyyyy"))?.toBigInteger(),
                valorTransferencia = transacaoPendente.transferenciaValor,
                documentoDebito = transacaoPendente.documentoDebito,
                documentoCredito = transacaoPendente.documentoCredito,
                codigoFinalidadeDOC = transacaoPendente.codigoFinalidadeDOC?.toBigInteger(),
                codigoFinalidadeTED = transacaoPendente.codigoFinalidadeTED?.toBigInteger(),
                numeroDepositoJudicial = transacaoPendente.numeroDepositoJudicial,
                descricaoTransferencia = transacaoPendente.descricaoTransferencia
            )
        }
    }
}