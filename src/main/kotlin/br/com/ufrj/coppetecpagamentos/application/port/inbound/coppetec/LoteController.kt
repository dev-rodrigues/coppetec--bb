package br.com.ufrj.coppetecpagamentos.application.port.inbound.coppetec

import br.com.ufrj.coppetecpagamentos.domain.service.ConsultarLoteService
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBLoteRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LogLoteEnviadoEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LoteEnviadoEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LoteEnviadoEntityPaginated
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.LoteRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController
@RequestMapping("/api/lote")
class LoteController(
        private val loteRepository: LoteRepository,
        private val bbLoteRepository: BBLoteRepository,
        private val consultarLoteService: ConsultarLoteService,
) {

    @GetMapping
    fun getAll(): ResponseEntity<List<LoteEnviadoEntity>> {
        val response = loteRepository.findAll()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{loteId}")
    fun getLotById(@PathVariable loteId: BigInteger): ResponseEntity<Void> {
        val lote = bbLoteRepository.findById(loteId).get();
        consultarLoteService.executar(
                lote = lote,
                step = 1,
        )
        return ResponseEntity.ok().build()
    }

    @GetMapping("/paginated")
    fun getPaginatedLote(
            @RequestParam("pageNumber", defaultValue = "1") pageNumber: Int,
            @RequestParam("pageSize", defaultValue = "10") pageSize: Int,
    ): ResponseEntity<LoteEnviadoEntityPaginated> {
        val response = loteRepository.findPaginated(pageNumber, pageSize)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/log/{loteId}")
    fun getLog(@PathVariable loteId: BigInteger): ResponseEntity<List<LogLoteEnviadoEntity>> {
        val response = loteRepository.findLogBy(loteId)
        return ResponseEntity.ok(response)
    }
}