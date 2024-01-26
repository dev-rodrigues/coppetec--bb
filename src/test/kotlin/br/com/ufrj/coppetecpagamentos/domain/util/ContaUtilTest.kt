package br.com.ufrj.coppetecpagamentos.domain.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ContaUtilTest {
    @Test
    fun `should get dv of agencia 2234-9`() {
        val agencia = "2234-9"
        val dv = ContaUtil.getDV(agencia)
        assertEquals("9", dv)
    }

    @Test
    fun `should get dv of agencia 22349`() {
        val agencia = "2234-9"
        val dv = ContaUtil.getDV(agencia)
        assertEquals("9", dv)
    }

    @Test
    fun `should get dv of cc 50155-7`() {
        val agencia = "50155-7"
        val dv = ContaUtil.getDV(agencia)
        assertEquals("7", dv)
    }

    @Test
    fun `should get dv of cc 10664-X`() {
        val agencia = "10664-X"
        val dv = ContaUtil.getDV(agencia)
        assertEquals("X", dv)
    }

    @Test
    fun `should get ag 2234-9 to ag full`() {
        val agencia = "2234-9"
        val agenciaFull = ContaUtil.getAgenciaFull(agencia)
        assertEquals("022349", agenciaFull)
    }

    @Test
    fun `should get cc 39878-0 to completed`() {
        val cc = "39.878"
        val ccCompleted = ContaUtil.getCC(cc)
        assertEquals("000000039878", ccCompleted)
    }
}