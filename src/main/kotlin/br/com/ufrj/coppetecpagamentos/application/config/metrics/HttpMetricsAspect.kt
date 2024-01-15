package br.com.ufrj.coppetecpagamentos.application.config.metrics

import io.micrometer.core.instrument.MeterRegistry
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Aspect
@Component
class HttpMetricsAspect @Autowired constructor(private val meterRegistry: MeterRegistry) {


    @Before("execution(* br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.impl.BBPortImpl.autenticar(..))")
    fun beforeAutenticar(joinPoint: JoinPoint): ResponseEntity<*> {
        val methodName = joinPoint.signature.name

        try {
            val result = (joinPoint as ProceedingJoinPoint).proceed()

            meterRegistry.counter("autenticar.requests.success", "method", methodName).increment()

            return result as ResponseEntity<*>
        } catch (ex: Exception) {
            meterRegistry.counter("autenticar.requests.failure", "method", methodName).increment()
            throw ex
        }
    }

    @Before("execution(* br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.impl.BBPortImpl.liberarLote(..))")
    fun beforeLiberarLote(joinPoint: JoinPoint): ResponseEntity<*> {
        val methodName = joinPoint.signature.name

        try {
            val result = (joinPoint as ProceedingJoinPoint).proceed()

            meterRegistry.counter("liberarLote.requests.success", "method", methodName).increment()

            return result as ResponseEntity<*>
        } catch (ex: Exception) {
            meterRegistry.counter("liberarLote.requests.failure", "method", methodName).increment()
            throw ex
        }
    }

    @Before("execution(* br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.impl.BBPortImpl.consultarLote(..))")
    fun beforeConsultarLote(joinPoint: JoinPoint): ResponseEntity<*> {
        val methodName = joinPoint.signature.name

        try {
            val result = (joinPoint as ProceedingJoinPoint).proceed()

            meterRegistry.counter("consultarLote.requests.success", "method", methodName).increment()

            return result as ResponseEntity<*>
        } catch (ex: Exception) {
            meterRegistry.counter("consultarLote.requests.failure", "method", methodName).increment()
            throw ex
        }
    }

    @Before("execution(* br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.impl.BBPortImpl.consultarTransferencia(..))")
    fun beforeConsultarTransferencia(joinPoint: JoinPoint): ResponseEntity<*> {
        val methodName = joinPoint.signature.name

        try {
            val result = (joinPoint as ProceedingJoinPoint).proceed()

            meterRegistry.counter("consultarTransferencia.requests.success", "method", methodName).increment()

            return result as ResponseEntity<*>
        } catch (ex: Exception) {
            meterRegistry.counter("consultarTransferencia.requests.failure", "method", methodName).increment()
            throw ex
        }
    }

    @Before("execution(* br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.impl.BBPortImpl.consultarExtrato(..))")
    fun beforeConsultarExtrato(joinPoint: JoinPoint): ResponseEntity<*> {
        val methodName = joinPoint.signature.name

        try {
            val result = (joinPoint as ProceedingJoinPoint).proceed()

            meterRegistry.counter("consultarExtrato.requests.success", "method", methodName).increment()

            return result as ResponseEntity<*>
        } catch (ex: Exception) {
            meterRegistry.counter("consultarExtrato.requests.failure", "method", methodName).increment()
            throw ex
        }
    }

    @Before("execution(* br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.impl.BBPortImpl.transferir(..))")
    fun beforeTransferir(joinPoint: JoinPoint): ResponseEntity<*> {
        val methodName = joinPoint.signature.name

        try {
            val result = (joinPoint as ProceedingJoinPoint).proceed()

            meterRegistry.counter("transferir.requests.success", "method", methodName).increment()

            return result as ResponseEntity<*>
        } catch (ex: Exception) {
            meterRegistry.counter("transferir.requests.failure", "method", methodName).increment()
            throw ex
        }
    }
}