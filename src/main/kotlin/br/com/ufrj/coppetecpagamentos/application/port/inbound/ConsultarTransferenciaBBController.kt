package br.com.ufrj.coppetecpagamentos.application.port.inbound

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
    private val bbPort: BBPort
) {

    @GetMapping("/{identificadorTransferencia}")
    fun get(
        @PathVariable identificadorTransferencia: BigInteger
    ): ResponseEntity<BBConsultaTransferenciaResponseDto> {
        val token = bbPort.autenticar()
        val response =
            bbPort.consultarTransferencia(identificadorTransferencia, requireNotNull(token.body?.accessToken))
        
        return ResponseEntity.ok(response)
    }

}