package br.com.ufrj.coppetecpagamentos.domain.service

import br.com.ufrj.coppetecpagamentos.domain.model.API
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBConsultaExtratoResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import org.springframework.stereotype.Service

@Service
class ExtratoService(
    private val bbPort: BBPort
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
}