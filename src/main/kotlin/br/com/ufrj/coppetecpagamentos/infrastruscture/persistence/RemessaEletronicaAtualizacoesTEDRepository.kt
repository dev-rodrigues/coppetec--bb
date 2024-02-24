package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.impl.RemessaEletronicaAtualizacoesTED

interface RemessaEletronicaAtualizacoesTEDRepository {
    fun getRemessasEletronicasAtualizacoesTED(): List<RemessaEletronicaAtualizacoesTED>
}