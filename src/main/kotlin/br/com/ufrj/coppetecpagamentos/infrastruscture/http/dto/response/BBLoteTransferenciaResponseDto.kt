package br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigInteger

data class BBLoteResponseDto(
    @JsonProperty("estadoRequisicao")
    val estadoRequisicao: Int? = null,

    @JsonProperty("quantidadeTransferencias")
    val quantidadeTransferencias: Int? = null,

    @JsonProperty("valorTransferencias")
    val valorTransferencias: Float? = null,

    @JsonProperty("quantidadeTransferenciasValidas")
    val quantidadeTransferenciasValidas: Int? = null,

    @JsonProperty("valorTransferenciasValidas")
    val valorTransferenciasValidas: Float? = null,

    @JsonProperty("transferencias")
    val transferencias: List<BBTransferenciaDto>
)

data class BBTransferenciaDto(

    @JsonProperty("identificadorTransferencia")
    var identificadorTransferencia: BigInteger? = null,

    @JsonProperty("tipoCredito")
    val tipoCredito: BigInteger? = null,

    @JsonProperty("numeroCOMPE")
    val numeroCOMPE: Int? = null,

    @JsonProperty("numeroISPB")
    val numeroISPB: Int? = null,

    @JsonProperty("agenciaCredito")
    val agenciaCredito: Int? = null,

    @JsonProperty("contaCorrenteCredito")
    val contaCorrenteCredito: Int? = null,

    @JsonProperty("digitoVerificadorContaCorrente")
    val digitoVerificadorContaCorrente: String? = null,

    @JsonProperty("contaPagamentoCredito")
    val contaPagamentoCredito: String? = null,

    @JsonProperty("cpfBeneficiario")
    val cpfBeneficiario: Long? = null,

    @JsonProperty("cnpjBeneficiario")
    val cnpjBeneficiario: Long? = null,

    @JsonProperty("dataTransferencia")
    val dataTransferencia: Int? = null,

    @JsonProperty("valorTransferencia")
    val valorTransferencia: Int? = null,

    @JsonProperty("documentoDebito")
    val documentoDebito: BigInteger? = null,

    @JsonProperty("documentoCredito")
    val documentoCredito: Int? = null,

    @JsonProperty("numeroDepositoJudicial")
    val numeroDepositoJudicial: String? = null,

    @JsonProperty("descricaoTransferencia")
    val descricaoTransferencia: String? = null,

    @JsonProperty("indicadorAceite")
    val indicadorAceite: String? = null,

    @JsonProperty("codigoFinalidadeDOC")
    val codigoFinalidadeDOC: String? = null,

    @JsonProperty("codigoFInalidadeTED")
    val codigoFinalidadeTED: String? = null,

    @JsonProperty("erros")
    val erros: List<Int>? = null
)