package br.com.ufrj.coppetecpagamentos.application.port.inbound.bb

import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.request.BBLiberacaoLoteRequest
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBLiberacaoLoteResponse
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/liberar-pagamento")
class LiberarLoteBBController(
    private val bbPort: BBPort
) {

    @PostMapping
    fun post(
        @RequestBody body: BBLiberacaoLoteRequest
    ): ResponseEntity<BBLiberacaoLoteResponse> {
//        val token = bbPort.autenticar(header = header).body!!.accessToken

//        val response = bbPort.liberarLote(body, token)

        return ResponseEntity.ok().build()
    }
}