package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBContasAtivas

interface BBContasAtivasRepository {
    fun getContas(): List<BBContasAtivas>
}