package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.impl

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.ErroTransferenciaEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.TransferenciaEnviadaEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.TransferenciaRepository
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.math.BigInteger
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component
@EnableTransactionManagement
@PropertySource("classpath:/queries/AdminBB.xml")
class TransferenciaRepositoryImpl(
    private val queries: Environment,
    @PersistenceContext private val em: EntityManager
) : TransferenciaRepository {

    override fun findAllByLoteId(loteId: BigInteger): List<TransferenciaEnviadaEntity> {
        val query = queries.getProperty("[COPPETEC].[bancoDoBrasil_remessa].[consultaTransferenciaDoLote]")
        val nativeQuery = em.createNativeQuery(query)
        nativeQuery.setParameter(1, loteId)
        val result = nativeQuery.resultList as List<Array<Any>>
        return TransferenciaEnviadaEntity.map(result)
    }

    override fun findErroBy(transferenciaId: BigInteger): List<ErroTransferenciaEntity> {
        val query = queries.getProperty("[COPPETEC].[bancoDoBrasil_remessa].[erroTransferencia]")
        val nativeQuery = em.createNativeQuery(query)
        nativeQuery.setParameter(1, transferenciaId)
        val result = nativeQuery.resultList as List<Array<Any>>
        return ErroTransferenciaEntity.map(result)
    }
}