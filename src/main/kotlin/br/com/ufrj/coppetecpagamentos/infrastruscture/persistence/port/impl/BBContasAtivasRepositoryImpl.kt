package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.impl

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.BBContasAtivasRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBContasAtivas
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component
@EnableTransactionManagement
@PropertySource("classpath:/queries/Contas.xml")
class BBContasAtivasRepositoryImpl(
    private val queries: Environment,
    @PersistenceContext
    private val em: EntityManager
) : BBContasAtivasRepository {
    override fun getContas(): List<BBContasAtivas> {
        val query = queries.getProperty("[COPPETEC].[bancoDoBrasil].[getContaAConsultar]")
        val nativeQuery = em.createNativeQuery(query)
        val result = nativeQuery.resultList as List<Array<Any>>
        return BBContasAtivas.map(result)
    }
}