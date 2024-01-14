package br.com.ufrj.coppetecpagamentos

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
class CoppetecPagamentosApplication

fun main(args: Array<String>) {
	runApplication<CoppetecPagamentosApplication>(*args)
}
