package br.com.ufrj.coppetecpagamentos.infrastruscture.client

import br.com.ufrj.coppetecpagamentos.application.config.FeignRetrieverConfig
import br.com.ufrj.coppetecpagamentos.domain.model.CreateLogRequestDto
import br.com.ufrj.coppetecpagamentos.domain.model.CreateLogResponseDto
import br.com.ufrj.coppetecpagamentos.domain.model.LogHeaderDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
        value = "\${services.log.url}",
        configuration = [FeignRetrieverConfig::class]
)
interface LogClient {

    @RequestMapping(
            method = [RequestMethod.GET],
            value = ["/header"],
            produces = ["application/json"],
            consumes = ["application/json"]
    )
    fun getHeader(): ResponseEntity<LogHeaderDto>

    @RequestMapping(
            method = [RequestMethod.POST],
            value = ["/log"],
            produces = ["application/json"],
            consumes = ["application/json"]
    )
    fun createLog(
            @RequestBody body: CreateLogRequestDto,
    ): ResponseEntity<CreateLogResponseDto>
}