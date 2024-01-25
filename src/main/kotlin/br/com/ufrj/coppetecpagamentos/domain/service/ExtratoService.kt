package br.com.ufrj.coppetecpagamentos.domain.service

import br.com.ufrj.coppetecpagamentos.domain.model.API
import br.com.ufrj.coppetecpagamentos.domain.util.DateUtil
import br.com.ufrj.coppetecpagamentos.domain.util.DateUtil.formatter
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBConsultaExtratoResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.ConciliacaoBancariaImportacaoEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.ConciliacaoBancariaMovimentoEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBContasAtivas
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.ConciliacaoBancariaImportacaoEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.ConciliacaoBancariaMovimentoEntity
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.LocalDateTime

@Service
class ExtratoService(
    private val bbPort: BBPort,
    private val conciliacaoRepository: ConciliacaoBancariaImportacaoEntityRepository,
    private val movimentoEntityRepository: ConciliacaoBancariaMovimentoEntityRepository
) {
    fun getExtrato(
        agencia: String,
        conta: String,
    ): BBConsultaExtratoResponseDto? {
        val token = bbPort.autenticar(API.EXTRATO).body?.accessToken!!

        var nextPage = 1
        var extrato: BBConsultaExtratoResponseDto? = null

        while (nextPage != 0) {
            val response = bbPort.consultarExtrato(
                agencia = agencia,
                conta = conta,
                token = token,
                numeroPaginaSolicitacao = nextPage
            )

            if (extrato != null) {
                val aux = response.body!!
                val lancamento = aux.listaLancamento
                extrato.listaLancamento.addAll(lancamento)

                nextPage = aux.numeroPaginaProximo
            } else {
                extrato = response.body!!

                nextPage = extrato.numeroPaginaProximo
            }

        }

        return extrato
    }

    fun register(consulta: BBContasAtivas, response: BBConsultaExtratoResponseDto) {
        val importacao =
            conciliacaoRepository.save(
                ConciliacaoBancariaImportacaoEntity(
                    id = null,
                    idLayOut = BigInteger.TWO,
                    idDocumento = null,
                    bancoOrigem = "001",
                    arquivoNome = "IMPORTED WITH API",
                    arquivoGeracaoDataHora = LocalDateTime.now(),
                    arquivoNumeroSequencial = BigInteger.ONE,
                    arquivoNumeroVersaoLayOut = "NaN",
                    qtdLotes = 1,
                    qtdRegistros = response.listaLancamento.size,
                    qtdContas = 1,
                    dataHora = LocalDateTime.now(),
                    idUsuario = BigInteger.ONE,
                    consultaAgencia = consulta.agenciaSemDv?.replace(".", ""),
                    consultaContaCorrente = consulta.contaCorrente,
                    consultaPeriodoDe = LocalDateTime.parse(consulta.consultaPeriodoDe!!, formatter),
                    consultaPeriodoAte = LocalDateTime.parse(consulta.consultaPeriodoAte!!, formatter)
                )
            )


        movimentoEntityRepository.saveAll(
            response.listaLancamento.map {
                ConciliacaoBancariaMovimentoEntity(
                    id = null,
                    idImportacao = importacao.id,

                )
            }
        )

    }
}