package br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.math.BigInteger

data class BBTransferenciaResponseDto(
    @SerializedName("estadoRequisicao") var estadoRequisicao: Int?,
    @SerializedName("quantidadeTransferencias") var quantidadeTransferencias: Int?,
    @SerializedName("valorTransferencias") var valorTransferencias: BigDecimal?,
    @SerializedName("quantidadeTransferenciasValidas") var quantidadeTransferenciasValidas: Int?,
    @SerializedName("valorTransferenciasValidas") var valorTransferenciasValidas: BigDecimal?,
    @SerializedName("transferencias") var transferencias: List<Transferencia>
)

data class Transferencia(
    @SerializedName("identificadorTransferencia") val identificadorTransferencia: BigInteger?,
    @SerializedName("tipoCredito") val tipoCredito: BigInteger?,
    @SerializedName("numeroCOMPE") val numeroCOMPE: BigInteger?,
    @SerializedName("numeroISPB") val numeroISPB: BigInteger?,
    @SerializedName("agenciaCredito") val agenciaCredito: BigInteger?,
    @SerializedName("contaCorrenteCredito") val contaCorrenteCredito: BigInteger?,
    @SerializedName("digitoVerificadorContaCorrente") val digitoVerificadorContaCorrente: String?,
    @SerializedName("contaPagamentoCredito") val contaPagamentoCredito: String?,
    @SerializedName("cpfBeneficiario") val cpfBeneficiario: BigInteger?,
    @SerializedName("cnpjBeneficiario") val cnpjBeneficiario: BigInteger?,
    @SerializedName("dataTransferencia") val dataTransferencia: BigInteger?,
    @SerializedName("valorTransferencia") val valorTransferencia: BigDecimal?,
    @SerializedName("documentoDebito") val documentoDebito: BigInteger?,
    @SerializedName("documentoCredito") val documentoCredito: BigInteger?,
    @SerializedName("numeroDepositoJudicial") val numeroDepositoJudicial: String?,
    @SerializedName("descricaoTransferencia") val descricaoTransferencia: String?,
    @SerializedName("indicadorAceite") val indicadorAceite: String?,
    @SerializedName("codigoFinalidadeDOC") val codigoFinalidadeDOC: String?,
    @SerializedName("codigoFinalidadeTED") val codigoFinalidadeTED: String?,
    @SerializedName("erros") val erros: List<Int> = listOf()
)