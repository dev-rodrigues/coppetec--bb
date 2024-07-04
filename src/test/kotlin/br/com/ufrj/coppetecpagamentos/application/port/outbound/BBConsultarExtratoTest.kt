package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.model.LogHeaderDto
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import br.com.ufrj.coppetecpagamentos.domain.service.ExtratoService
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessType
import br.com.ufrj.coppetecpagamentos.domain.singleton.SchedulerExecutionTracker
import br.com.ufrj.coppetecpagamentos.fixture.getBBConsultaExtratoResponseDto
import br.com.ufrj.coppetecpagamentos.fixture.getBBContasAtivas
import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBContasAtivasRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TogglePort
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.verify
import org.springframework.http.ResponseEntity
import java.math.BigInteger
import java.math.BigInteger.ONE

@ExtendWith(MockKExtension::class)
class BBConsultarExtratoTest {

    private val bBContasAtivasRepository: BBContasAtivasRepository = mockk()
    private val extratoService: ExtratoService = mockk()
    private val meterRegistry: MeterRegistry = mockk()
    private val togglePort: TogglePort = mockk()
    private val properties: ScheduleProperties = mockk()
    private val executionTracker: SchedulerExecutionTracker = mockk()
    private val logClient: LogClient = mockk()

    private val service = BBConsultarExtrato(
            bBContasAtivasRepository = bBContasAtivasRepository,
            extratoService = extratoService,
            meterRegistry = meterRegistry,
            togglePort = togglePort,
            properties = properties,
            logClient = logClient
    )

    @Test
    fun `should send to register bank statement`() {

//        every {
//            togglePort.isEnabled(any())
//        } returns true
//
//        every {
//            logClient.getHeader()
//        } returns ResponseEntity.ok().body(LogHeaderDto(
//                id = ONE,
//                dataHora = "2021-09-01T00:00:00Z"
//        ))
//
//        justRun {
//            executionTracker.recordExecutionStart(ProcessType.BANK_STATEMENT_INQUIRY_PROCESS)
//        }
//
//        justRun {
//            executionTracker.recordExecutionEnd(ProcessType.BANK_STATEMENT_INQUIRY_PROCESS)
//        }
//
//        every {
//            properties.schedule
//        } returns true
//
//        every {
//            bBContasAtivasRepository.getContas()
//        } returns listOf(
//                getBBContasAtivas()
//        )
//
//        justRun {
//            meterRegistry.counter(any(), any(), any()).increment()
//        }
//
//        every {
//            extratoService.getExtrato(any(), any(), any(), any(), ONE)
//        } returns getBBConsultaExtratoResponseDto()
//
//        every {
//            logClient.createLog(any())
//        } returns ResponseEntity.ok().body(null)
//
//        justRun {
//            extratoService.register(any(), any(), any())
//        }
//
//        service.getExtrato()
//
//        verify(exactly = 1) {
//            extratoService.register(any(), any(), any())
//        }
    }

    @Test
    fun `should not execute register when extratoService returned null`() {
        every {
            logClient.getHeader()
        } returns ResponseEntity.ok().body(LogHeaderDto(
                id = ONE,
                dataHora = "2021-09-01T00:00:00Z"
        ))

        every {
            togglePort.isEnabled(any())
        } returns false

        justRun {
            executionTracker.recordExecutionStart(ProcessType.BANK_STATEMENT_INQUIRY_PROCESS)
        }

        justRun {
            executionTracker.recordExecutionEnd(ProcessType.BANK_STATEMENT_INQUIRY_PROCESS)
        }

        every {
            properties.schedule
        } returns true

        every {
            bBContasAtivasRepository.getContas()
        } returns listOf(
                getBBContasAtivas()
        )

        justRun {
            meterRegistry.counter(any(), any(), any()).increment()
        }

        justRun {
            logClient.createLog(any())
        }

        every {
            extratoService.getExtrato(any(), any(), any(), any(), ONE)
        } returns null

        service.getExtrato()

        verify(exactly = 0) {
            extratoService.register(any(), any(), any())
        }
    }

}