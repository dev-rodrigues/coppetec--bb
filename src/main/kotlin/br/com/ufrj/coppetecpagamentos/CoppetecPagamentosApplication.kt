package br.com.ufrj.coppetecpagamentos

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@EnableAsync
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
class CoppetecPagamentosApplication

fun main(args: Array<String>) {
	System.setProperty("jdk.tls.maxHandshakeMessageSize", "65536")
	runApplication<CoppetecPagamentosApplication>(*args)
}
