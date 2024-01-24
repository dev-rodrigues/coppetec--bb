package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBContasAtivasRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BBConsultarExtrato(
    private val bBContasAtivasRepository: BBContasAtivasRepository
) {
    @Scheduled(cron = "0 0 8 * * ?")
    fun execute() {
        val agora = LocalDateTime.now()
        val contas = bBContasAtivasRepository.getContas()
        println(agora)
        println(contas)
    }
}