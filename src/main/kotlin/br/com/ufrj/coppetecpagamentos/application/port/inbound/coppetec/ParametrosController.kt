package br.com.ufrj.coppetecpagamentos.application.port.inbound.coppetec

import br.com.ufrj.coppetecpagamentos.domain.model.ToggleModel
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TogglePort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/parametros")
class ParametrosController(
    private val togglePort: TogglePort
) {

    @GetMapping
    fun get(): ResponseEntity<List<ToggleModel>> {
        return ResponseEntity.ok(togglePort.getToggles())
    }

    @PatchMapping
    fun patch(@RequestBody body: ToggleModel): ResponseEntity<ToggleModel> {
        return ResponseEntity.ok(togglePort.patch(body))
    }
}