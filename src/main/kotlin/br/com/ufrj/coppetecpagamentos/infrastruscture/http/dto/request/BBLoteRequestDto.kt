package br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.request

import java.math.BigInteger

data class BBLoteRequestDto(
    val numeroRequisicao: BigInteger? = null,
    val numeroContratoPagamento: String? = null,
    val agenciaDebito: Int? = null,
    val contaCorrenteDebito: Int? = null,
    val digitoVerificadorContaCorrente: Int? = null,
    val tipoPagamento: Int? = null,
    val listaTransferencias: List<BBTransferenciaRequestDto>? = null
)

data class BBTransferenciaRequestDto(
    val numeroCOMPE: Int? = null,
    val numeroISPB: Int? = null,
    val agenciaCredito: Int? = null,
    val contaCorrenteCredito: Int? = null,
    val digitoVerificadorContaCorrente: String? = null,
    val contaPagamentoCredito: String? = null,
    val cpfBeneficiario: Long? = null,
    val cnpjBeneficiario: Long? = null,
    val dataTransferencia: Int? = null,
    val valorTransferencia: Double? = null,
    val documentoDebito: BigInteger? = null,
    val documentoCredito: Int? = null,
    val codigoFinalidadeDOC: Int? = null,
    val codigoFinalidadeTED: Int? = null,
    val numeroDepositoJudicial: String? = null,
    val descricaoTransferencia: String? = null
)