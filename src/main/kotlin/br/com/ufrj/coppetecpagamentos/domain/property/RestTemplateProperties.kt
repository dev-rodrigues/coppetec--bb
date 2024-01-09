package br.com.ufrj.coppetecpagamentos.domain.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "resttemplate")
data class RestTemplateProperties(
    var readTimeout: Int = 0,
    var connectionTimeout: Int = 0,
    var password: String = "",
    var protocol: String = "",
    var maxRetry: Int = 0,
    var retryInterval: Int = 0,
    var keyStoreInstance: String = "",
    var classPath: String = ""
)