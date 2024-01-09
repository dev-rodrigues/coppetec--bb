package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.impl

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.TransferenciaPendenteDatabase
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LoteEnvioPendenteDatabase
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.EnvioPendentePort
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component
@EnableTransactionManagement
@PropertySource("classpath:/queries/BancoDoBrasilRemessa.xml")
class EnvioPendentePortImpl(
    private val queries: Environment,
    @PersistenceContext
    private val em: EntityManager
) : EnvioPendentePort {

    override fun getEnvioPendenteDatabase(): List<LoteEnvioPendenteDatabase> {
        val query = queries.getProperty("[COPPETEC].[bancoDoBrasil_remessa].[getRemessaEletronicaPendente]")
        val nativeQuery = em.createNativeQuery(query)
        val result = nativeQuery.resultList as List<Array<Any>>
        return LoteEnvioPendenteDatabase.map(result)
    }

    override fun getTransferenciasPendente(contaFonte: String, tipoPagamento: String): List<TransferenciaPendenteDatabase> {
        val query = queries.getProperty("[bancoDoBrasil_remessa].[getRemessaEletronicaPendente_Lancamentos]")
        val nativeQuery = em.createNativeQuery(query)
        nativeQuery.setParameter(1, contaFonte)
        nativeQuery.setParameter(2, tipoPagamento)
        val result = nativeQuery.resultList as List<Array<Any>>
        return TransferenciaPendenteDatabase.map(result)
    }
}