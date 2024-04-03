package br.com.ufrj.coppetecpagamentos.application.config

import br.com.ufrj.coppetecpagamentos.domain.exception.FeignRetryException
import feign.RetryableException
import feign.Retryer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("DEPRECATED_IDENTITY_EQUALS")
open class GenericFeignClientRetriever(
        private var retryMaxAttempt: Int,
        private var retryInterval: Long
) : Retryer {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private var attempt = 1

    override fun clone(): Retryer {
        return GenericFeignClientRetriever(retryMaxAttempt, retryInterval)
    }

    override fun continueOrPropagate(e: RetryableException) {
        log.info("Feign tentando reconectar pela {}° vez, por conta de {} ", attempt, e.message)

        if (attempt++ === retryMaxAttempt) throw FeignRetryException(this.retryMaxAttempt, this.retryInterval, e.message)

        try {
            Thread.sleep(retryInterval)
        } catch (ignored: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }
}