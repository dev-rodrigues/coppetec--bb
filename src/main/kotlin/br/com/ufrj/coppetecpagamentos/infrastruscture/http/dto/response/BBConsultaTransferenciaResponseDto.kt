package br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.math.BigInteger

data class BBConsultaTransferenciaResponseDto(
    @SerializedName("id") val id: BigInteger?,
    @SerializedName("estadoPagamento") val estadoPagamento: String? = null,
    @SerializedName("tipoCredito") val tipoCredito: BigInteger?,
    @SerializedName("agenciaDebito") val agenciaDebito: BigInteger?,
    @SerializedName("contaCorrenteDebito") val contaCorrenteDebito: BigInteger?,
    @SerializedName("digitoVerificadorContaCorrente") val digitoVerificadorContaCorrente: String?,
    @SerializedName("inicioCartaoCredito") val inicioCartaoCredito: BigInteger?,
    @SerializedName("fimCartaoCredito") val fimCartaoCredito: BigInteger?,
    @SerializedName("dataPagamento") val dataPagamento: String?,
    @SerializedName("valorPagamento") val valorPagamento: BigDecimal?,
    @SerializedName("documentoDebito") val documentoDebito: BigInteger?,
    @SerializedName("codigoAutenticacaoPagamento") val codigoAutenticacaoPagamento: String?,
    @SerializedName("numeroDepositoJudicial") val numeroDepositoJudicial: String?,
    @SerializedName("codigoFinalidadeDOC") val codigoFinalidadeDOC: String?,
    @SerializedName("codigoFinalidadeTED") val codigoFinalidadeTED: String?,
    @SerializedName("listaPagamentos") val listaPagamentos: List<PagamentoDetalhe>?,
    @SerializedName("listaDevolucao") val listaDevolucao: List<Devolucao>?
)

data class PagamentoDetalhe(
    @SerializedName("numeroCOMPE") val numeroCOMPE: BigInteger?,
    @SerializedName("numeroISPB") val numeroISPB: BigInteger?,
    @SerializedName("agenciaCredito") val agenciaCredito: BigInteger?,
    @SerializedName("contaCorrenteCredito") val contaCorrenteCredito: BigInteger?,
    @SerializedName("digitoVerificadorContaCorrente") val digitoVerificadorContaCorrente: String?,
    @SerializedName("numeroContaCredito") val numeroContaCredito: String?,
    @SerializedName("tipoBeneficiario") val tipoBeneficiario: BigInteger?,
    @SerializedName("cpfCnpjBeneficiario") val cpfCnpjBeneficiario: BigInteger?,
    @SerializedName("nomeBeneficiario") val nomeBeneficiario: String?,
    @SerializedName("documentoCredito") val documentoCredito: BigInteger?,
    @SerializedName("texto") val texto: String?
)

data class Devolucao(
    @SerializedName("codigoMotivo") val codigoMotivo: BigInteger?,
    @SerializedName("dataDevolucao") val dataDevolucao: String?,
    @SerializedName("valorDevolucao") val valorDevolucao: BigDecimal?
)