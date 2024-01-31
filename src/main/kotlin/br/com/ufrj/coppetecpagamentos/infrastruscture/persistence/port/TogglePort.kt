package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port

import br.com.ufrj.coppetecpagamentos.domain.model.ToggleModel

interface TogglePort {
    fun isEnabled(toggle: Int): Boolean
    fun getToggles(): List<ToggleModel>
    fun patch(toggle: ToggleModel): ToggleModel
}