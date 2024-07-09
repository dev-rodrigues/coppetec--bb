package br.com.ufrj.coppetecpagamentos.domain.util

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigInteger
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class DateUtilTest {

    @Test
    fun `should map date`() {
        val date = "2024-01-24T00:00:00"
        assertDoesNotThrow { LocalDateTime.parse(date, DateUtil.formatter) }
    }

    @Test
    fun `should map 25012024 bb date`() {
        val bbDate = 25012024L
        val response = DateUtil.formatDate(bbDate)
        val expected = LocalDateTime.of(
            /* year = */ 2024,
            /* month = */ 1,
            /* dayOfMonth = */ 25,
            /* hour = */ 0,
            /* minute = */ 0,
            /* second = */ 0
        )
        assert(response == expected)
    }

    @Test
    fun `should map 25112024 bb date`() {
        val bbDate = 25112024L
        val response = DateUtil.formatDate(bbDate)
        val expected = LocalDateTime.of(
            /* year = */ 2024,
            /* month = */ 11,
            /* dayOfMonth = */ 25,
            /* hour = */ 0,
            /* minute = */ 0,
            /* second = */ 0
        )
        assert(response == expected)
    }

    @Test
    fun `should map 1012024L bb date`() {
        val bbDate = 1012024L
        val response = DateUtil.formatDate(bbDate)
        val expected = LocalDateTime.of(
            /* year = */ 2024,
            /* month = */ 1,
            /* dayOfMonth = */ 1,
            /* hour = */ 0,
            /* minute = */ 0,
            /* second = */ 0
        )
        assert(response == expected)
    }

    @Test
    fun `formatDate should handle 8 digit date correctly`() {
        val input = "25012024"
        val expected = LocalDateTime.of(2024, 1, 25, 0, 0)
        val result = DateUtil.formatDate(input)
        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `formatDate should handle 7 digit date correctly by prepending zero`() {
        val input = "1012024"
        val expected = LocalDateTime.of(2024, 1, 1, 0, 0)
        val result = DateUtil.formatDate(input)
        Assertions.assertEquals(expected, result)
    }

    @Test
    fun `other formatDate should handle 7 digit date correctly by prepending zero`() {
        val input = "8072024"
        val expected = LocalDateTime.of(2024, 7, 8, 0, 0)
        val result = DateUtil.formatDate(input)
        Assertions.assertEquals(expected, result)
    }

}