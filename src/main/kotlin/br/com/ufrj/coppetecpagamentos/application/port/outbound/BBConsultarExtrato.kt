package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.service.ExtratoService
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBContasAtivasRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BBConsultarExtrato(
    private val bBContasAtivasRepository: BBContasAtivasRepository,
    private val extratoService: ExtratoService,
) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(
        fixedDelay = BBEnviarLoteJob.MINUTO,
        zone = BBEnviarLoteJob.TIME_ZONE
    )
    fun execute() {
        val contas = bBContasAtivasRepository.getContas()

        contas.forEach {
            try {
                val result = extratoService
                    .getExtrato(
                        agencia = it.agenciaSemDv!!,
                        conta = it.contaCorrenteSemDv!!
                    )

                result?.let { response ->
                    extratoService.register(it, response)
                }
            } catch (e: Exception) {
                log.error(
                    "ERRO AO CONSULTAR EXTRATO: AG ${it.agencia} CC: ${it.contaCorrente} - ERRO: ${e.message}"
                )
            }
        }
    }
}