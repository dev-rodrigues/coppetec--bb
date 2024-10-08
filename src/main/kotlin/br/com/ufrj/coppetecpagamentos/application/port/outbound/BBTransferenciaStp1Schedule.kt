package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.model.Toggle.BB_TRANSFERENCIA_STP1_SCHEDULE
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import br.com.ufrj.coppetecpagamentos.domain.service.EnviarLoteService
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessType.PAYMENT_SENDING_PROCESS
import br.com.ufrj.coppetecpagamentos.domain.singleton.SchedulerExecutionTracker
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.TransferenciaPendenteDatabase
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.EnvioPendentePort
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TogglePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BBTransferenciaStp1Schedule(
    private val enviarLoteService: EnviarLoteService,
    private val envioPendentePort: EnvioPendentePort,
    private val togglePort: TogglePort,
    private val properties: ScheduleProperties,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val parts: Int = 250
    private var isRunning = false

    @Scheduled(
        fixedDelay = 1000, zone = BBTransferenciaStp2Schedule.TIME_ZONE
    )
    fun step1() {
        if (isRunning) {
            logger.error("STEP 4: JÁ ESTÁ EM EXECUÇÃO")
            return
        }

        try {
            isRunning = true
            SchedulerExecutionTracker.getInstance().recordExecutionStart(PAYMENT_SENDING_PROCESS)
            val active = properties.schedule && togglePort.isEnabled(BB_TRANSFERENCIA_STP1_SCHEDULE)

            if (active) {

                val remessas = envioPendentePort.getEnvioPendenteDatabase()

                remessas.forEachIndexed { index, it ->

                    val transferencias = envioPendentePort.getTransferenciasPendente(
                        contaFonte = it.contaOrigem!!,
                        tipoPagamento = it.tipoPagamento!!
                    )

                    val parts: List<List<TransferenciaPendenteDatabase>> = transferencias.chunked(parts)

                    parts.forEachIndexed { indexPart, part ->
                        enviarLoteService.executar(it, part)
                    }
                }
            }
        } finally {
            isRunning = false
            SchedulerExecutionTracker.getInstance().recordExecutionEnd(PAYMENT_SENDING_PROCESS)
        }
    }
}