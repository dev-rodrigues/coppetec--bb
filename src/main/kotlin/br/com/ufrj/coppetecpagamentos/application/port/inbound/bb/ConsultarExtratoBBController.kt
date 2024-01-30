package br.com.ufrj.coppetecpagamentos.application.port.inbound.bb

import br.com.ufrj.coppetecpagamentos.application.port.outbound.BBConsultarExtrato
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/bb/extrato")
class ConsultarExtratoBBController(
    private val bBConsultarExtrato: BBConsultarExtrato
) {
    @GetMapping()
    fun get(): ResponseEntity<Void> {

        CompletableFuture.runAsync {
            bBConsultarExtrato.execute()
        }

        return ResponseEntity.ok().build()
    }
}