package br.com.ufrj.coppetecpagamentos.application.port.inbound.bb

import br.com.ufrj.coppetecpagamentos.domain.service.ExtratoService
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBConsultaExtratoResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/bb/extrato")
class ConsultarExtratoBBController(
    private val extratoService: ExtratoService
) {
    @GetMapping("/{agencia}/{conta}")
    fun get(
        @PathVariable agencia: String,
        @PathVariable conta: String,
    ): ResponseEntity<BBConsultaExtratoResponseDto> {

        val response = extratoService.getExtrato(
            agencia = agencia,
            conta = conta
        )

        return ResponseEntity.ok(response)
    }
}