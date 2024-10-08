package br.com.ufrj.coppetecpagamentos.application.port.inbound.bb

import br.com.ufrj.coppetecpagamentos.domain.model.API
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBAutenticacaoResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/autenticar")
class AutenticacaoBBController(
        private val bbPort: BBPort,
) {

    @GetMapping
    fun autenticar(): ResponseEntity<BBAutenticacaoResponseDto> {
        val response = bbPort.autenticar(API.EXTRATO)
        return ResponseEntity.ok(response.body)
    }
}