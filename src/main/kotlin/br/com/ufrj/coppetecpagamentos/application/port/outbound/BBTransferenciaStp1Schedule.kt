package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.model.Toggle.BB_TRANSFERENCIA_STP1_SCHEDULE
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import br.com.ufrj.coppetecpagamentos.domain.service.EnviarLoteService
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessType.PAYMENT_SENDING_PROCESS
import br.com.ufrj.coppetecpagamentos.domain.singleton.SchedulerExecutionTracker
import br.com.ufrj.coppetecpagamentos.domain.singleton.TransferLog
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.TransferenciaPendenteDatabase
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.EnvioPendentePort
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TogglePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
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
    private val parts: Int = 300
    private var isRunning = false

    @Async
    @Scheduled(
            fixedDelay = 60 * 1000, zone = BBTransferenciaStp2Schedule.TIME_ZONE
    )
    fun step1() {
        if (isRunning) {
            logger.error("STEP 4: JÁ ESTÁ EM EXECUÇÃO")
            return
        }

        try {
            isRunning = true
            val active = true// properties.schedule && togglePort.isEnabled(BB_TRANSFERENCIA_STP1_SCHEDULE)

            if (active) {
                try {
                    val remessas = envioPendentePort.getEnvioPendenteDatabase()

                    SchedulerExecutionTracker.getInstance().recordExecutionStart(PAYMENT_SENDING_PROCESS)

                    logger.info("STEP 1: TOTAL DE TRANSFERÊNCIAS PENDENTES DE ENVIO {} ", remessas.size)

                    SchedulerExecutionTracker.getInstance().addLogTransfer(PAYMENT_SENDING_PROCESS, TransferLog(
                            message = "STEP 1: TOTAL DE TRANSFERÊNCIAS PENDENTES DE ENVIO ${remessas.size}",
                    ))

                    remessas.forEachIndexed { index, it ->

                        SchedulerExecutionTracker.getInstance().addLogTransfer(PAYMENT_SENDING_PROCESS, TransferLog(
                                message = "STEP 1: PROCESSANDO REMESSA ${index + 1} DE ${remessas.size}",
                        ))

                        val transferencias = envioPendentePort.getTransferenciasPendente(
                                contaFonte = it.contaOrigem!!, tipoPagamento = it.tipoPagamento!!
                        )

                        SchedulerExecutionTracker.getInstance().addLogTransfer(PAYMENT_SENDING_PROCESS, TransferLog(
                                message = "STEP 1: REMESSA ${index + 1} DE ${remessas.size} - TOTAL DE TRANSFERÊNCIAS ${transferencias.size}"
                        ))

                        val parts: List<List<TransferenciaPendenteDatabase>> = transferencias.chunked(parts)

                        SchedulerExecutionTracker.getInstance().addLogTransfer(PAYMENT_SENDING_PROCESS, TransferLog(
                                message = "STEP 1: REMESSA ${index + 1} DE ${remessas.size} - GERADO ${parts.size} PARTES DE TRANSFERÊNCIA"
                        ))

                        logger.info(
                                "NUMERO DE TRANSFERENCIAS: {} - GERADO {} PARTES DE TRANSFERENCIA",
                                transferencias.size,
                                parts.size
                        )

                        parts.forEachIndexed { indexPart, part ->

                            SchedulerExecutionTracker.getInstance().addLogTransfer(PAYMENT_SENDING_PROCESS, TransferLog(
                                    message = "STEP 1: REMESSA ${index + 1} DE ${remessas.size} - PROCESSANDO PARTE ${indexPart + 1} DE ${parts.size}"
                            ))

                            enviarLoteService.executar(it, part)
                        }
                    }
                } finally {
                    SchedulerExecutionTracker.getInstance().recordExecutionEnd(PAYMENT_SENDING_PROCESS)
                }

            } else {
                logger.warn("STEP 1: ENVIO DE TRANSFERÊNCIAS PENDENTES DESABILITADO")
            }
        } finally {
            isRunning = false
        }
    }
}