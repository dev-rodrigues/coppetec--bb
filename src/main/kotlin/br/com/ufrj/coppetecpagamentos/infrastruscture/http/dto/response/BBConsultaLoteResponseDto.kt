package br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.math.BigInteger

data class BBConsultaLoteResponseDto(
    @SerializedName("estadoRequisicao")
    val estadoRequisicao: Int,
    @SerializedName("quantidadePagamentos")
    val quantidadePagamentos: Int,
    @SerializedName("valorPagamentos")
    val valorPagamentos: BigDecimal,
    @SerializedName("quantidadePagamentosValidos")
    val quantidadePagamentosValidos: Int,
    @SerializedName("valorPagamentosValidos")
    val valorPagamentosValidos: BigDecimal,
    @SerializedName("pagamentos")
    val pagamentos: List<Pagamento>
)

data class Pagamento(
    @SerializedName("identificadorPagamento")
    val identificadorPagamento: BigInteger,
    @SerializedName("numeroCOMPE")
    val numeroCOMPE: BigInteger,
    @SerializedName("numeroISPB")
    val numeroISPB: BigInteger,
    @SerializedName("agenciaCredito")
    val agenciaCredito: BigInteger,
    @SerializedName("contaCorrenteCredito")
    val contaCorrenteCredito: Int,
    @SerializedName("digitoVerificadorContaCorrente")
    val digitoVerificadorContaCorrente: String,
    @SerializedName("contaPagamentoCredito")
    val contaPagamentoCredito: String,
    @SerializedName("cpfBeneficiario")
    val cpfBeneficiario: BigInteger,
    @SerializedName("cnpjBeneficiario")
    val cnpjBeneficiario: BigInteger,
    @SerializedName("dataPagamento")
    val dataPagamento: BigInteger,
    @SerializedName("valorPagamento")
    val valorPagamento: Int,
    @SerializedName("documentoDebito")
    val documentoDebito: BigInteger,
    @SerializedName("documentoCredito")
    val documentoCredito: BigInteger,
    @SerializedName("tipoCredito")
    val tipoCredito: BigInteger,
    @SerializedName("codigoFinalidadeDOC")
    val codigoFinalidadeDOC: String,
    @SerializedName("codigoFinalidadeTED")
    val codigoFinalidadeTED: String,
    @SerializedName("numeroDepositoJudicial")
    val numeroDepositoJudicial: String,
    @SerializedName("descricaoPagamento")
    val descricaoPagamento: String,
    @SerializedName("indicadorAceite")
    val indicadorAceite: String,
    @SerializedName("erros")
    val erros: List<Int>
)