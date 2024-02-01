package br.com.ufrj.coppetecpagamentos.application.config

import br.com.ufrj.coppetecpagamentos.domain.property.BBProperties
import br.com.ufrj.coppetecpagamentos.domain.property.RestTemplateProperties
import br.com.ufrj.coppetecpagamentos.domain.property.ScheduleProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [
    BBProperties::class,
    RestTemplateProperties::class,
    ScheduleProperties::class
])
class AppPropertiesConfig {
    //
}