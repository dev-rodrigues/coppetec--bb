package br.com.ufrj.coppetecpagamentos.application.port.inbound.coppetec

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.TransferenciaEnviadaEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TransferenciaRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController
@RequestMapping("/api/transferencias")
class TransferenciasDoLoteController(
    private val transferenciaRepository: TransferenciaRepository
) {
    @GetMapping("/lote/{loteId}")
    fun getByLoteId(@PathVariable loteId: BigInteger): ResponseEntity<List<TransferenciaEnviadaEntity>> {
        val response = transferenciaRepository.findAllByLoteId(loteId)
        return ResponseEntity.ok(response)
    }
}