package br.com.ufrj.coppetecpagamentos.fixture

import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBConsultaExtratoResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.Lancamento
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBContasAtivas

fun getBBConsultaExtratoResponseDto(
    numeroPaginaAtual: Int = 1,
    quantidadeRegistroPaginaAtual: Int = 1,
    numeroPaginaAnterior: Int = 0,
    numeroPaginaProximo: Int = 0,
    quantidadeTotalPagina: Int = 1,
    quantidadeTotalRegistro: Int = 1,
    listaLancamento: MutableList<Lancamento> = mutableListOf()
) = BBConsultaExtratoResponseDto(
    numeroPaginaAtual = 1,
    quantidadeRegistroPaginaAtual = 1,
    numeroPaginaAnterior = 0,
    numeroPaginaProximo = 0,
    quantidadeTotalPagina = 1,
    quantidadeTotalRegistro = 1,
    listaLancamento = mutableListOf()
)

fun getBBContasAtivas(
    banco: String = "001",
    agencia: String = "0001",
    agenciaSemDv: String = "0001",
    contaCorrente: String = "0000000001",
    contaCorrenteSemDv: String = "0000000001",
    consultaPeriodoDe: String = "2021-01-01T00:00:00",
    consultaPeriodoAte: String = "2021-01-01T00:00:00",
) = BBContasAtivas(
    banco = banco,
    agencia = agencia,
    agenciaSemDv = agenciaSemDv,
    contaCorrente = contaCorrente,
    contaCorrenteSemDv = contaCorrenteSemDv,
    consultaPeriodoDe = consultaPeriodoDe,
    consultaPeriodoAte = consultaPeriodoAte,
)