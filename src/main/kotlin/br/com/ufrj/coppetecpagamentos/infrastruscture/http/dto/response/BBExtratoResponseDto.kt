package br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class BBExtratoResponseDto(
    @JsonProperty("numeroPaginaAtual") val numeroPaginaAtual: Int,
    @JsonProperty("quantidadeRegistroPaginaAtual") val quantidadeRegistroPaginaAtual: Int,
    @JsonProperty("numeroPaginaAnterior") val numeroPaginaAnterior: Int,
    @JsonProperty("numeroPaginaProximo") val numeroPaginaProximo: Int,
    @JsonProperty("quantidadeTotalPagina") val quantidadeTotalPagina: Int,
    @JsonProperty("quantidadeTotalRegistro") val quantidadeTotalRegistro: Int,
    @JsonProperty("listaLancamento") val listaLancamento: LancamentoExtrato
)

data class LancamentoExtrato(
    @JsonProperty("indicadorTipoLancamento") val indicadorTipoLancamento: String,
    @JsonProperty("dataLancamento") val dataLancamento: Long,
    @JsonProperty("dataMovimento") val dataMovimento: Long,
    @JsonProperty("codigoAgenciaOrigem") val codigoAgenciaOrigem: Int,
    @JsonProperty("numeroLote") val numeroLote: Int,
    @JsonProperty("numeroDocumento") val numeroDocumento: Long,
    @JsonProperty("codigoHistorico") val codigoHistorico: Int,
    @JsonProperty("textoDescricaoHistorico") val textoDescricaoHistorico: String,
    @JsonProperty("valorLancamento") val valorLancamento: Double,
    @JsonProperty("indicadorSinalLancamento") val indicadorSinalLancamento: String,
    @JsonProperty("textoInformacaoComplementar") val textoInformacaoComplementar: String,
    @JsonProperty("numeroCpfCnpjContrapartida") val numeroCpfCnpjContrapartida: Long,
    @JsonProperty("indicadorTipoPessoaContrapartida") val indicadorTipoPessoaContrapartida: String,
    @JsonProperty("codigoBancoContrapartida") val codigoBancoContrapartida: Int,
    @JsonProperty("codigoAgenciaContrapartida") val codigoAgenciaContrapartida: Int,
    @JsonProperty("numeroContaContrapartida") val numeroContaContrapartida: String,
    @JsonProperty("textoDvContaContrapartida") val textoDvContaContrapartida: String
)