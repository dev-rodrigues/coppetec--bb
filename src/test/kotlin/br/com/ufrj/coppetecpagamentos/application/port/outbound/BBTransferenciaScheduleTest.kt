package br.com.ufrj.coppetecpagamentos.application.port.outbound

import br.com.ufrj.coppetecpagamentos.domain.model.LogHeaderDto
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import br.com.ufrj.coppetecpagamentos.domain.service.EnviarLoteService
import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LoteEnvioPendenteDatabase
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.TransferenciaPendenteDatabase
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.EnvioPendentePort
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TogglePort
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import java.math.BigInteger
import java.math.BigInteger.ONE
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class BBTransferenciaScheduleTest() {

    private val envioPendentePort: EnvioPendentePort = mockk()
    private val enviarLoteService: EnviarLoteService = mockk()
    private val togglePort: TogglePort = mockk()
    private val properties: ScheduleProperties = mockk()
    private val logClient: LogClient = mockk()

    private val schedule = BBTransferenciaStp1Schedule(
        envioPendentePort = envioPendentePort,
        enviarLoteService = enviarLoteService,
        togglePort = togglePort,
        properties = properties,
        logClient = logClient
    )


//    @Test
//    fun `should sent single part to enviarLoteService`() {
//        every {
//            togglePort.isEnabled(any())
//        } returns true
//
//        every {
//            properties.schedule
//        } returns true
//
//        every {
//            logClient.getHeader()
//        } returns ResponseEntity.ok().body(
//            LogHeaderDto(
//                id = ONE,
//                dataHora = "2021-09-01T00:00:00Z"
//            )
//        )
//
//        every {
//            logClient.createLog(any())
//        } returns ResponseEntity.ok().body(null)
//
//        every {
//            envioPendentePort.getEnvioPendenteDatabase()
//        } returns listOf(
//            LoteEnvioPendenteDatabase(
//                banco = "banco",
//                agenciaDebito = "agenciaDebito",
//                digitoAgenciaDebito = "digitoAgenciaDebito",
//                contaDebito = "contaDebito",
//                contaDebitoDigito = "contaDebitoDigito",
//                contaOrigem = "contaOrigem",
//                tipoPagamento = "tipoPagamento",
//                contratoDePagamento = "contratoDePagamento",
//                compe = "compe",
//                ispb = "ispb",
//            )
//        )
//
//        every {
//            envioPendentePort.getTransferenciasPendente(
//                contaFonte = any(),
//                tipoPagamento = any(),
//            )
//        } returns listOf(
//            TransferenciaPendenteDatabase(
//                banco = "",
//                agenciaDebito = "",
//                agenciaDebitoDigito = "",
//                contaDebito = "",
//                contaDebitoDigito = "",
//                contaFonte = "",
//                tipoPagamento = "",
//                numeroCOMPE = "",
//                numeroISPB = "",
//                beneficiarioBanco = "",
//                beneficiarioAgencia = ONE,
//                beneficiarioContaCorrente = ONE,
//                beneficiarioContaCorrenteDV = "",
//                beneficiarioCPF = "",
//                beneficiarioCNPJ = "",
//                transferenciaData = LocalDate.now(),
//                transferenciaValor = BigDecimal.ONE,
//                documentoDebito = ONE,
//                documentoCredito = ONE,
//                codigoFinalidadeDOC = "",
//                codigoFinalidadeTED = "",
//                numeroDepositoJudicial = "",
//                descricaoTransferencia = ""
//            )
//        )
//
//        justRun {
//            enviarLoteService.executar(any(), any())
//        }
//
//        schedule.step1()
//
//        verify(exactly = 1) {
//            enviarLoteService.executar(any(), any())
//        }
//    }
//
//    @Test
//    fun `should sent tree part to enviarLoteService`() {
//        every {
//            togglePort.isEnabled(any())
//        } returns true
//
//        every {
//            logClient.getHeader()
//        } returns ResponseEntity.ok().body(
//            LogHeaderDto(
//                id = ONE,
//                dataHora = "2021-09-01T00:00:00Z"
//            )
//        )
//
//        every {
//            logClient.createLog(any())
//        } returns ResponseEntity.ok().body(null)
//
//        every {
//            properties.schedule
//        } returns true
//
//        every {
//            envioPendentePort.getEnvioPendenteDatabase()
//        } returns listOf(
//            LoteEnvioPendenteDatabase(
//                banco = "banco",
//                agenciaDebito = "agenciaDebito",
//                digitoAgenciaDebito = "digitoAgenciaDebito",
//                contaDebito = "contaDebito",
//                contaDebitoDigito = "contaDebitoDigito",
//                contaOrigem = "contaOrigem",
//                tipoPagamento = "tipoPagamento",
//                contratoDePagamento = "contratoDePagamento",
//                compe = "compe",
//                ispb = "ispb",
//            )
//        )
//
//
//        every {
//            envioPendentePort.getTransferenciasPendente(
//                contaFonte = any(),
//                tipoPagamento = any(),
//            )
//        } returns List(900) {
//            TransferenciaPendenteDatabase(
//                banco = "",
//                agenciaDebito = "",
//                agenciaDebitoDigito = "",
//                contaDebito = "",
//                contaDebitoDigito = "",
//                contaFonte = "",
//                tipoPagamento = "",
//                numeroCOMPE = "",
//                numeroISPB = "",
//                beneficiarioBanco = "",
//                beneficiarioAgencia = ONE,
//                beneficiarioContaCorrente = ONE,
//                beneficiarioContaCorrenteDV = "",
//                beneficiarioCPF = "",
//                beneficiarioCNPJ = "",
//                transferenciaData = LocalDate.now(),
//                transferenciaValor = BigDecimal.ONE,
//                documentoDebito = ONE,
//                documentoCredito = ONE,
//                codigoFinalidadeDOC = "",
//                codigoFinalidadeTED = "",
//                numeroDepositoJudicial = "",
//                descricaoTransferencia = ""
//            )
//        }
//
//        justRun {
//            enviarLoteService.executar(any(), any())
//        }
//
//        schedule.step1()
//
//        verify(exactly = 3) {
//            enviarLoteService.executar(any(), any())
//        }
//    }
//
//    @Test
//    fun `should sent four part to enviarLoteService`() {
//        every {
//            togglePort.isEnabled(any())
//        } returns true
//
//        every {
//            logClient.getHeader()
//        } returns ResponseEntity.ok().body(
//            LogHeaderDto(
//                id = ONE,
//                dataHora = "2021-09-01T00:00:00Z"
//            )
//        )
//
//        every {
//            logClient.createLog(any())
//        } returns ResponseEntity.ok().body(null)
//
//        every {
//            properties.schedule
//        } returns true
//
//        every {
//            envioPendentePort.getEnvioPendenteDatabase()
//        } returns listOf(
//            LoteEnvioPendenteDatabase(
//                banco = "banco",
//                agenciaDebito = "agenciaDebito",
//                digitoAgenciaDebito = "digitoAgenciaDebito",
//                contaDebito = "contaDebito",
//                contaDebitoDigito = "contaDebitoDigito",
//                contaOrigem = "contaOrigem",
//                tipoPagamento = "tipoPagamento",
//                contratoDePagamento = "contratoDePagamento",
//                compe = "compe",
//                ispb = "ispb",
//            )
//        )
//
//
//        every {
//            envioPendentePort.getTransferenciasPendente(
//                contaFonte = any(),
//                tipoPagamento = any(),
//            )
//        } returns List(901) {
//            TransferenciaPendenteDatabase(
//                banco = "",
//                agenciaDebito = "",
//                agenciaDebitoDigito = "",
//                contaDebito = "",
//                contaDebitoDigito = "",
//                contaFonte = "",
//                tipoPagamento = "",
//                numeroCOMPE = "",
//                numeroISPB = "",
//                beneficiarioBanco = "",
//                beneficiarioAgencia = ONE,
//                beneficiarioContaCorrente = ONE,
//                beneficiarioContaCorrenteDV = "",
//                beneficiarioCPF = "",
//                beneficiarioCNPJ = "",
//                transferenciaData = LocalDate.now(),
//                transferenciaValor = BigDecimal.ONE,
//                documentoDebito = ONE,
//                documentoCredito = ONE,
//                codigoFinalidadeDOC = "",
//                codigoFinalidadeTED = "",
//                numeroDepositoJudicial = "",
//                descricaoTransferencia = ""
//            )
//        }
//
//        justRun {
//            enviarLoteService.executar(any(), any())
//        }
//
//        schedule.step1()
//
//        verify(exactly = 4) {
//            enviarLoteService.executar(any(), any())
//        }
//    }
//
//    @Test
//    fun `should sent five part to enviarLoteService`() {
//        every {
//            togglePort.isEnabled(any())
//        } returns true
//
//        every {
//            properties.schedule
//        } returns true
//
//        every {
//            logClient.getHeader()
//        } returns ResponseEntity.ok().body(
//            LogHeaderDto(
//                id = ONE,
//                dataHora = "2021-09-01T00:00:00Z"
//            )
//        )
//
//        every {
//            logClient.createLog(any())
//        } returns ResponseEntity.ok().body(null)
//
//        every {
//            envioPendentePort.getEnvioPendenteDatabase()
//        } returns listOf(
//            LoteEnvioPendenteDatabase(
//                banco = "banco",
//                agenciaDebito = "agenciaDebito",
//                digitoAgenciaDebito = "digitoAgenciaDebito",
//                contaDebito = "contaDebito",
//                contaDebitoDigito = "contaDebitoDigito",
//                contaOrigem = "contaOrigem",
//                tipoPagamento = "tipoPagamento",
//                contratoDePagamento = "contratoDePagamento",
//                compe = "compe",
//                ispb = "ispb",
//            )
//        )
//
//
//        every {
//            envioPendentePort.getTransferenciasPendente(
//                contaFonte = any(),
//                tipoPagamento = any(),
//            )
//        } returns List(1500) {
//            TransferenciaPendenteDatabase(
//                banco = "",
//                agenciaDebito = "",
//                agenciaDebitoDigito = "",
//                contaDebito = "",
//                contaDebitoDigito = "",
//                contaFonte = "",
//                tipoPagamento = "",
//                numeroCOMPE = "",
//                numeroISPB = "",
//                beneficiarioBanco = "",
//                beneficiarioAgencia = ONE,
//                beneficiarioContaCorrente = ONE,
//                beneficiarioContaCorrenteDV = "",
//                beneficiarioCPF = "",
//                beneficiarioCNPJ = "",
//                transferenciaData = LocalDate.now(),
//                transferenciaValor = BigDecimal.ONE,
//                documentoDebito = ONE,
//                documentoCredito = ONE,
//                codigoFinalidadeDOC = "",
//                codigoFinalidadeTED = "",
//                numeroDepositoJudicial = "",
//                descricaoTransferencia = ""
//            )
//        }
//
//        justRun {
//            enviarLoteService.executar(any(), any())
//        }
//
//        schedule.step1()
//
//        verify(exactly = 5) {
//            enviarLoteService.executar(any(), any())
//        }
//    }

}