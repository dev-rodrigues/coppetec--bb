package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.impl

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LogLoteEnviadoEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LoteEnviadoEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LoteEnviadoEntityPaginated
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LoteEnviadoEntityPaginatedProp
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.LoteRepository
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
class LoteRepositoryImpl(
    private val queries: Environment,
    @PersistenceContext
    private val em: EntityManager
) : LoteRepository {
    override fun findAll(): List<LoteEnviadoEntity> {
        val query = queries.getProperty("[COPPETEC].[bancoDoBrasil_remessa].[consultaLoteEnviado]")
        val nativeQuery = em.createNativeQuery(query)
        val result = nativeQuery.resultList as List<Array<Any>>
        return LoteEnviadoEntity.map(result)
    }

    override fun findPaginated(page: Int, size: Int): LoteEnviadoEntityPaginated {
        val query = queries.getProperty("[COPPETEC].[bancoDoBrasil_remessa].[consultaLoteEnviadoPaginated]")
        val nativeQuery = em.createNativeQuery(query)
        nativeQuery.setParameter("pageSize", size)
        nativeQuery.setParameter("offset", (page - 1) * size)
        val result = nativeQuery.resultList as List<Array<Any>>

        return LoteEnviadoEntityPaginated(
            content = LoteEnviadoEntity.map(result),
            pageable = findPaginatedProps(
                page = page,
                size = size
            )
        )
    }

    private fun findPaginatedProps(page: Int, size: Int): LoteEnviadoEntityPaginatedProp {
        val query = queries.getProperty("[COPPETEC].[bancoDoBrasil_remessa].[consultaLoteEnviadoPaginated.[props]")
        val nativeQuery = em.createNativeQuery(query)
        nativeQuery.setParameter("pageSize", size)
        nativeQuery.setParameter("pageNumber", page)

        val result = nativeQuery.singleResult
        return LoteEnviadoEntityPaginatedProp.map(result)
    }

    override fun findLogBy(loteId: BigInteger): List<LogLoteEnviadoEntity> {
        val query = queries.getProperty("[COPPETEC].[bancoDoBrasil_remessa].[logLote]")
        val nativeQuery = em.createNativeQuery(query)
        nativeQuery.setParameter(1, loteId)
        val result = nativeQuery.resultList as List<Array<Any>>
        return LogLoteEnviadoEntity.map(result)
    }
}