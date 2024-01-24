package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.service.ExtratoService
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBContasAtivasRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BBConsultarExtrato(
    private val bBContasAtivasRepository: BBContasAtivasRepository,
    private val extratoService: ExtratoService,
) {
    @Scheduled(
        fixedDelay = BBEnviarLoteJob.MINUTO,
        zone = BBEnviarLoteJob.TIME_ZONE
    )
    fun execute() {
        val contas = bBContasAtivasRepository.getContas()

        contas.forEach {
            val result = extratoService.getExtrato(
                agencia = it.agenciaSemDv!!,
                conta = it.contaCorrenteSemDv!!
            )
        }
    }
}