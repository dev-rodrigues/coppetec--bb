package br.com.ufrj.coppetecpagamentos.application.port.inbound.coppetec

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.ErroTransferenciaEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TransferenciaRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController
@RequestMapping("/api/erro")
class ErroTransferenciaController(
    private val transferenciaRepository: TransferenciaRepository
) {

    @GetMapping("/transferencia/{transferenciaId}")
    fun get(@PathVariable transferenciaId: BigInteger): ResponseEntity<List<ErroTransferenciaEntity>> {
        val response = transferenciaRepository.findErroBy(transferenciaId)
        return ResponseEntity.ok(response)
    }
}