package br.com.ufrj.coppetecpagamentos.application.port.inbound

import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/bb/extrato")
class ConsultarExtratoBBController(
    private val bbPort: BBPort
) {


    @GetMapping("/{agencia}/{conta}")
    fun get(
        @PathVariable agencia: String, @PathVariable conta: String
    ): ResponseEntity<String> {

        val token = bbPort.autenticar().body?.accessToken!!

        val response = bbPort.consultarExtrato(agencia, conta, token)

        return ResponseEntity.ok(response)
    }
}