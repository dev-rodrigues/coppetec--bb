package br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response

import kotlinx.serialization.SerialName
import java.math.BigDecimal

data class BBConsultaExtratoResponseDto(
    @SerialName("numeroPaginaAtual")
    val numeroPaginaAtual: Int,

    @SerialName("quantidadeRegistroPaginaAtual")
    val quantidadeRegistroPaginaAtual: Int,

    @SerialName("numeroPaginaAnterior")
    val numeroPaginaAnterior: Int,

    @SerialName("numeroPaginaProximo")
    val numeroPaginaProximo: Int,

    @SerialName("quantidadeTotalPagina")
    val quantidadeTotalPagina: Int,

    @SerialName("quantidadeTotalRegistro")
    val quantidadeTotalRegistro: Int,

    @SerialName("listaLancamento")
    val listaLancamento: List<Lancamento>
)

data class Lancamento(
    @SerialName("indicadorTipoLancamento")
    val indicadorTipoLancamento: String,

    @SerialName("dataLancamento")
    val dataLancamento: Long,

    @SerialName("dataMovimento")
    val dataMovimento: Long,

    @SerialName("codigoAgenciaOrigem")
    val codigoAgenciaOrigem: Int,

    @SerialName("numeroLote")
    val numeroLote: Int,

    @SerialName("numeroDocumento")
    val numeroDocumento: Long,

    @SerialName("codigoHistorico")
    val codigoHistorico: Int,

    @SerialName("textoDescricaoHistorico")
    val textoDescricaoHistorico: String,

    @SerialName("valorLancamento")
    val valorLancamento: BigDecimal,

    @SerialName("indicadorSinalLancamento")
    val indicadorSinalLancamento: String,

    @SerialName("textoInformacaoComplementar")
    val textoInformacaoComplementar: String,

    @SerialName("numeroCpfCnpjContrapartida")
    val numeroCpfCnpjContrapartida: Long,

    @SerialName("indicadorTipoPessoaContrapartida")
    val indicadorTipoPessoaContrapartida: String,

    @SerialName("codigoBancoContrapartida")
    val codigoBancoContrapartida: Int,

    @SerialName("codigoAgenciaContrapartida")
    val codigoAgenciaContrapartida: Int,

    @SerialName("numeroContaContrapartida")
    val numeroContaContrapartida: String,

    @SerialName("textoDvContaContrapartida")
    val textoDvContaContrapartida: String
)