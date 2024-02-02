package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.impl

import br.com.ufrj.coppetecpagamentos.domain.model.ToggleModel
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.ParametrosEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TogglePort
import org.springframework.stereotype.Component

@Component
class TogglePortImpl(
    private val parametros: ParametrosEntityRepository,
) : TogglePort {
    override fun isEnabled(toggle: Int): Boolean {
        val response = parametros.findById(1)
        return when (toggle) {
            1 -> response.get().enviarTransferencia!!
            2 -> response.get().consultarPrioridade!!
            3 -> response.get().consultarSemPrioridade!!
            4 -> response.get().consultarExtrato!!
            else -> false
        }
    }

    override fun getToggles(): List<ToggleModel> {
        val response = parametros.findById(1)
        return listOf(
            ToggleModel("envio", response.get().enviarTransferencia!!),
            ToggleModel("consultaPasso1", response.get().consultarPrioridade!!),
            ToggleModel("consultaPasso2", response.get().consultarSemPrioridade!!),
            ToggleModel("consultaExtrato", response.get().consultarExtrato!!)
        )
    }

    override fun patch(toggle: ToggleModel): ToggleModel {
        val response = parametros.findById(1)
        val entity = response.get()
        when (toggle.name) {
            "envio" -> entity.enviarTransferencia = toggle.state
            "consultaPasso1" -> entity.consultarPrioridade = toggle.state
            "consultaPasso2" -> entity.consultarSemPrioridade = toggle.state
            "consultaExtrato" -> entity.consultarExtrato = toggle.state
        }

        val updated = parametros.save(entity)

        return ToggleModel(toggle.name, updated.enviarTransferencia!!)
    }
}