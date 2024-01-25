package br.com.ufrj.coppetecpagamentos.domain.util

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class DateUtilTest {

    @Test
    fun `should map date`() {
        val date = "2024-01-24T00:00:00"
        assertDoesNotThrow { LocalDateTime.parse(date, DateUtil.formatter) }
    }
}