package br.com.ufrj.coppetecpagamentos.domain.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "enable")
data class ScheduleProperties(
    val schedule: Boolean
)