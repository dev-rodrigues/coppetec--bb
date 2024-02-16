package br.com.ufrj.coppetecpagamentos.application.port.inbound.bb

import br.com.ufrj.coppetecpagamentos.application.port.outbound.BBConsultarExtrato
import br.com.ufrj.coppetecpagamentos.domain.service.ExtratoService
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBConsultaExtratoResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/bb/extrato")
class ConsultarExtratoBBController(
    private val bBConsultarExtrato: BBConsultarExtrato,
    private val extratoService: ExtratoService
) {

    @GetMapping()
    fun get(): ResponseEntity<Void> {

        CompletableFuture.runAsync {
            bBConsultarExtrato.execute()
        }

        return ResponseEntity.ok().build()
    }

    @GetMapping("/{ag}/{cc}/{de}/{ate}")
    fun getBy(
        @PathVariable ag: String,
        @PathVariable cc: String,
        @PathVariable de: String,
        @PathVariable ate: String
    ): ResponseEntity<BBConsultaExtratoResponseDto?> {

        val r = extratoService.getExtrato(
            agencia = ag,
            conta = cc,
            dataInicioSolicitacao = de,
            dataFimSolicitacao = ate
        )

        return ResponseEntity.ok(r)
    }
}