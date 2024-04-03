package br.com.ufrj.coppetecpagamentos.application.config

import org.springframework.beans.factory.annotation.Value

class FeignRetrieverConfig(
        @Value("\${feign.client.default.config.numberRetry}") numberRetry: Int,
        @Value("\${feign.client.default.config.retryInterval}") retryInterval: Long,
) : GenericFeignClientRetriever(numberRetry, retryInterval)