package br.com.ufrj.coppetecpagamentos.application.port.inbound.bb

import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBConsultaTransferenciaResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController
@RequestMapping("/bb/transferencia")
class ConsultarTransferenciaBBController(
        private val bbPort: BBPort,
        private val logClient: LogClient,
) {

    @GetMapping("/{identificadorTransferencia}")
    fun get(
            @PathVariable identificadorTransferencia: BigInteger,
    ): ResponseEntity<BBConsultaTransferenciaResponseDto> {
        val id = logClient.getHeader().body!!.id
        val token = bbPort.autenticar(header = id)
        val response = bbPort.consultarTransferencia(
                identificadorTransferencia = identificadorTransferencia,
                accessToken = requireNotNull(token.body?.accessToken),
                header = id
        )
        return ResponseEntity.ok(response)
    }

}