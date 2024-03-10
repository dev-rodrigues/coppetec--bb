package br.com.ufrj.coppetecpagamentos.application.port.inbound.coppetec

import br.com.ufrj.coppetecpagamentos.domain.singleton.ExecutionRecord
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessStatus
import br.com.ufrj.coppetecpagamentos.domain.singleton.ProcessType
import br.com.ufrj.coppetecpagamentos.domain.singleton.SchedulerExecutionTracker
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/execution-tracker")
class ExecutionTracker(
) {

    @GetMapping
    fun get(): ResponseEntity<List<Response>> {
        val result = SchedulerExecutionTracker.getInstance().getProcessStatus()
        val response = result.map {
            Response(
                    processType = it.key,
                    processStatus = it.value
            )
        }
        return ResponseEntity.ok(
                response
        )
    }

    @GetMapping("/{process}")
    fun getByProcess(@PathVariable process: String): ResponseEntity<List<ExecutionRecord>> {
        val result = SchedulerExecutionTracker.getInstance().getLogProcess(ProcessType.valueOf(process))

        return ResponseEntity.ok(
                result
        )
    }
}

data class Response(
        val processType: ProcessType,
        val processStatus: ProcessStatus
)

