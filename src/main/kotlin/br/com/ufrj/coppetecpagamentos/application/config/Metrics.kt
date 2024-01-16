package br.com.ufrj.coppetecpagamentos.application.config

import io.micrometer.core.instrument.MeterRegistry

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Metrics {
    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer { registry: MeterRegistry ->
            registry.config().commonTags("application", "pagamentos")
        }
    }
}