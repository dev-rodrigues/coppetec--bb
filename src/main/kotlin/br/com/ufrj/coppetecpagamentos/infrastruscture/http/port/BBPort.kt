package br.com.ufrj.coppetecpagamentos.infrastruscture.http.port

import br.com.ufrj.coppetecpagamentos.domain.model.API
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.request.BBLiberacaoLoteRequest
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.request.BBTransferirRequest
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.*
import org.springframework.http.ResponseEntity
import java.math.BigInteger

interface BBPort {
    fun autenticar(api: API = API.TRANSFERENCIA): ResponseEntity<BBAutenticacaoResponseDto>
    fun liberarLote(body: BBLiberacaoLoteRequest, token: String): ResponseEntity<BBLiberacaoLoteResponse>
    fun consultarLote(idLote: BigInteger, accessToken: String): ResponseEntity<BBConsultaLoteResponseDto>
    fun consultarTransferencia(
        identificadorTransferencia: BigInteger, accessToken: String
    ): BBConsultaTransferenciaResponseDto

    fun transferir(loteDeEnvio: BBTransferirRequest, token: String): ResponseEntity<BBTransferenciaResponseDto>?
    fun consultarExtrato(
        numeroPaginaSolicitacao: Int?,
        agencia: String,
        conta: String,
        token: String,
    ): ResponseEntity<BBConsultaExtratoResponseDto>
}