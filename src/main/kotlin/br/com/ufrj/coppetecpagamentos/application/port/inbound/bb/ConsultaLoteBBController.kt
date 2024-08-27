package br.com.ufrj.coppetecpagamentos.application.port.inbound.bb

import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBConsultaLoteResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController
@RequestMapping("/bb/lote")
class ConsultaLoteBBController(
        private val bbPort: BBPort,
) {
    @GetMapping("/{idLote}")
    fun get(@PathVariable idLote: BigInteger): ResponseEntity<BBConsultaLoteResponseDto> {
        val token = bbPort.autenticar()
        val response = bbPort.consultarLote(idLote, requireNotNull(token.body?.accessToken))
        return ResponseEntity.ok(response?.body)
    }
}