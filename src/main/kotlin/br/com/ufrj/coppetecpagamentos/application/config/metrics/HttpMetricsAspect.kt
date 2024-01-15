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


    @Before("execution(* br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.impl.BBPortImpl.*(..))")
    fun beforeApiCall(joinPoint: JoinPoint): ResponseEntity<*> {
        val methodName = joinPoint.signature.name

        try {
            val result = (joinPoint as ProceedingJoinPoint).proceed()

            meterRegistry.counter("${methodName.toLowerCase()}.requests.success", "method", methodName).increment()

            return result as ResponseEntity<*>
        } catch (ex: Exception) {
            meterRegistry.counter("${methodName.toLowerCase()}.requests.failure", "method", methodName).increment()
            throw ex
        }
    }
}