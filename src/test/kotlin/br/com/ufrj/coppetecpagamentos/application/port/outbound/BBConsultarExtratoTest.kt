package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.service.ExtratoService
import br.com.ufrj.coppetecpagamentos.fixture.getBBConsultaExtratoResponseDto
import br.com.ufrj.coppetecpagamentos.fixture.getBBContasAtivas
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBContasAtivasRepository
import io.micrometer.core.instrument.MeterRegistry
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import io.mockk.verify

@ExtendWith(MockKExtension::class)
class BBConsultarExtratoTest {

    private val bBContasAtivasRepository: BBContasAtivasRepository = mockk()
    private val extratoService: ExtratoService = mockk()
    private val meterRegistry: MeterRegistry = mockk()

    private val service = BBConsultarExtrato(
        bBContasAtivasRepository = bBContasAtivasRepository,
        extratoService = extratoService,
        meterRegistry = meterRegistry
    )

    @Test
    fun `should send to register bank statement`() {

        every {
            bBContasAtivasRepository.getContas()
        } returns listOf(
            getBBContasAtivas()
        )

        justRun {
            meterRegistry.counter(any(), any(), any()).increment()
        }

        every {
            extratoService.getExtrato(any(), any(), any(), any())
        } returns getBBConsultaExtratoResponseDto()

        justRun {
            extratoService.register(any(), any())
        }

        service.execute()

        verify(exactly = 1) {
            extratoService.register(any(), any())
        }
    }

    @Test
    fun `should not execute register when extratoService returned null`() {
        every {
            bBContasAtivasRepository.getContas()
        } returns listOf(
            getBBContasAtivas()
        )

        justRun {
            meterRegistry.counter(any(), any(), any()).increment()
        }

        every {
            extratoService.getExtrato(any(), any(), any(), any())
        } returns null

        service.execute()

        verify(exactly = 0) {
            extratoService.register(any(), any())
        }
    }

}