package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.TransferenciaPendenteDatabase
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LoteEnvioPendenteDatabase

interface EnvioPendentePort {
    fun getEnvioPendenteDatabase(): List<LoteEnvioPendenteDatabase>
    fun getTransferenciasPendente(contaFonte: String, tipoPagamento: String): List<TransferenciaPendenteDatabase>
}