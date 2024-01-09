package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Timestamp
import java.time.Month

@ExtendWith(MockKExtension::class)
class TransferenciaPendenteDatabaseTest {

    @Test
    fun `should format 2024-01-05 to local date`() {
        val responseMock = getTransferenciaFixture()
        TransferenciaPendenteDatabase.map(listOf(responseMock))
            .forEach {
                assert(it.transferenciaData?.dayOfMonth == 5)
                assert(it.transferenciaData?.month == Month.JANUARY)
                assert(it.transferenciaData?.year == 2024)
            }
    }


    private fun getTransferenciaFixture() =
        arrayOf<Any>(
            "Banco1",
            "1234",
            '5',
            "5678",
            '9',
            "Fonte",
            "Pagamento",
            "COMPE123",
            "ISPB456",
            "BenefBanco",
            BigInteger.valueOf(789),
            BigInteger.valueOf(101112),
            'D',
            "CPF12345678901",
            "CNPJ12345678901234",
            Timestamp.valueOf("2024-01-05 00:00:00"),
            BigDecimal("100.50"),
            BigInteger.valueOf(12345),
            BigInteger.valueOf(67890),
            "DOC",
            "TED",
            "Deposito123",
            "Descrição da transferência"
        )

}