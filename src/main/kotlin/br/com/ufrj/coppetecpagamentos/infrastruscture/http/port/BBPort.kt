package br.com.ufrj.coppetecpagamentos.infrastruscture.http.port

import br.com.ufrj.coppetecpagamentos.domain.model.API
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.request.BBTransferirRequest
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.*
import org.springframework.http.ResponseEntity
import java.math.BigInteger

interface BBPort {
    fun autenticar(
            api: API = API.TRANSFERENCIA,
//            header: BigInteger,
    ): ResponseEntity<BBAutenticacaoResponseDto>

    //    fun liberarLote(body: BBLiberacaoLoteRequest, token: String): ResponseEntity<BBLiberacaoLoteResponse>
    fun consultarLote(
            idLote: BigInteger,
            accessToken: String,
//            header: BigInteger,
    ): ResponseEntity<BBConsultaLoteResponseDto>?

    fun consultarTransferencia(
            identificadorTransferencia: BigInteger,
            accessToken: String,
//            header: BigInteger,
    ): BBConsultaTransferenciaResponseDto?

    fun transferir(
            loteDeEnvio: BBTransferirRequest,
            token: String,
//            headerBody: BigInteger,
    ): ResponseEntity<BBTransferenciaResponseDto>?

    fun consultarExtrato(
            numeroPaginaSolicitacao: Int?,
            agencia: String,
            conta: String,
            token: String,
            dataInicioSolicitacao: String,
            dataFimSolicitacao: String,
    ): ResponseEntity<BBConsultaExtratoResponseDto>
}