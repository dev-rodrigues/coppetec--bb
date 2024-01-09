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
    var agenciaDebito: Int,
    var contaCorrenteDebito: Int,
    var digitoVerificadorContaCorrente: Int,
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
                loteEnvioPendenteDatabase.agenciaDebito?.toInt()!!,
                loteEnvioPendenteDatabase.contaDebito?.toInt()!!,
                loteEnvioPendenteDatabase.contaDebitoDigito?.toInt()!!,
                loteEnvioPendenteDatabase.tipoPagamento?.toInt()!!,
                mutableListOf()
            )
        }
    }
}

data class BBTransferenciaRequest(
    var numeroCOMPE: Int? = null,
    var numeroISPB: Int? = null,
    var agenciaCredito: Int? = null,
    var contaCorrenteCredito: Int? = null,
    var digitoVerificadorContaCorrente: String? = null,
    var contaPagamentoCredito: String? = null,
    var cpfBeneficiario: Long? = null,
    var cnpjBeneficiario: Long? = null,
    var dataTransferencia: Int? = null,
    var valorTransferencia: BigDecimal? = null,
    var documentoDebito: Int? = null,
    var documentoCredito: Int? = null,
    var codigoFinalidadeDOC: Int? = null,
    var codigoFinalidadeTED: Int? = null,
    var numeroDepositoJudicial: String? = null,
    var descricaoTransferencia: String? = null,
) {
    companion object {
        fun mapTransferenciaRequest(transacaoPendente: TransferenciaPendenteDatabase): BBTransferenciaRequest {
            return BBTransferenciaRequest(
                numeroCOMPE = transacaoPendente.numeroCOMPE?.toInt(),
                numeroISPB = transacaoPendente.numeroISPB?.toInt(),
                agenciaCredito = transacaoPendente.beneficiarioAgencia?.toInt(),
                contaCorrenteCredito = transacaoPendente.beneficiarioContaCorrente?.toInt(),
                digitoVerificadorContaCorrente = transacaoPendente.beneficiarioContaCorrenteDV,
                contaPagamentoCredito = null,
                cpfBeneficiario = transacaoPendente.beneficiarioCPF?.toLong(),
                cnpjBeneficiario = transacaoPendente.beneficiarioCNPJ?.toLong(),
                dataTransferencia = transacaoPendente.transferenciaData?.format(DateTimeFormatter.ofPattern("ddMMyyyy"))?.toInt(),
                valorTransferencia = transacaoPendente.transferenciaValor,
                documentoDebito = transacaoPendente.documentoDebito?.toInt(),
                documentoCredito = transacaoPendente.documentoCredito?.toInt(),
                codigoFinalidadeDOC = transacaoPendente.codigoFinalidadeDOC?.toInt(),
                codigoFinalidadeTED = transacaoPendente.codigoFinalidadeTED?.toInt(),
                numeroDepositoJudicial = transacaoPendente.numeroDepositoJudicial,
                descricaoTransferencia = transacaoPendente.descricaoTransferencia
            )
        }
    }
}