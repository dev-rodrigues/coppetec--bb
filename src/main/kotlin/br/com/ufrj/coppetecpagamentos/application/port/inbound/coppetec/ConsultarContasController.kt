package br.com.ufrj.coppetecpagamentos.application.port.inbound.coppetec

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBContasAtivasRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBContasAtivas
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/contas-ativas")
class ConsultarContasController(
    private val bBContasAtivasRepository: BBContasAtivasRepository
) {

    @GetMapping
    fun get(): List<BBContasAtivas> {
        return bBContasAtivasRepository.getContas()
    }
}