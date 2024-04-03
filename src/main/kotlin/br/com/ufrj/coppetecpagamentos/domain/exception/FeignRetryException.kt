package br.com.ufrj.coppetecpagamentos.domain.exception

class FeignRetryException(
        val retryMaxAttempt: Int,
        val retryInterval: Long,
        message: String?
) : RuntimeException(message)