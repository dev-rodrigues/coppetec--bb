package br.com.ufrj.coppetecpagamentos.domain.common

import br.com.ufrj.coppetecpagamentos.domain.util.DateUtil
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigInteger
import java.time.LocalDateTime
import java.time.Month

@ExtendWith(MockKExtension::class)
class UtilsKtTest {

    @Test
    fun `should format 1012024 to local date`() {
        val dataResponse = BigInteger.valueOf(1012024)
        val result = formatarData(dataResponse)

        assert(result.year == 2024)
        assert(result.month == Month.JANUARY)
        assert(result.dayOfMonth == 1)
    }

    @Test
    fun `should format 10012024 local date`() {
        val dataResponse = BigInteger.valueOf(10012024)
        val result = formatarData(dataResponse)

        assert(result.year == 2024)
        assert(result.month == Month.JANUARY)
        assert(result.dayOfMonth == 10)
    }

    @Test
    fun `should format 10122024 local date`() {
        val dataResponse = BigInteger.valueOf(10122024)
        val result = formatarData(dataResponse)

        assert(result.year == 2024)
        assert(result.month == Month.DECEMBER)
        assert(result.dayOfMonth == 10)
    }

    @Test
    fun `format date`() {
        val data = "12012024"
        val response = formatarData(data.toBigInteger())
        println(response)
    }
}