package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port.impl

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.RemessaEletronicaAtualizacoesTEDRepository
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.math.BigInteger
import java.sql.Timestamp
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_DATE
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Component
@EnableTransactionManagement
@PropertySource("classpath:/queries/Contas.xml")
class RemessaEletronicaAtualizacoesTEDRepositoryImpl(
    private val queries: Environment,
    @PersistenceContext
    private val em: EntityManager
) : RemessaEletronicaAtualizacoesTEDRepository {
    override fun getRemessasEletronicasAtualizacoesTED(): List<RemessaEletronicaAtualizacoesTED> {
        val query = queries.getProperty("[COPPETEC].[bancoDoBrasil_remessa].[getRemessaEletronica_atualizacoesTED]")
        val nativeQuery = em.createNativeQuery(query)
        val result = nativeQuery.resultList as List<Array<Any>>
        return RemessaEletronicaAtualizacoesTED.map(result)
    }
}

data class RemessaEletronicaAtualizacoesTED(
    val loteId: BigInteger,
    val loteDataHora: String,
    val transferenciaId: BigInteger,
    val identificadorTransferencia: BigInteger
) {
    companion object {
        fun map(result: List<Array<Any>>): List<RemessaEletronicaAtualizacoesTED> {

            return if (result.isEmpty()) {
                emptyList()
            } else result.stream().map { it: Array<Any> ->
                RemessaEletronicaAtualizacoesTED(
                    loteId = (it[0] as BigInteger),
                    loteDataHora = ISO_DATE.format((it[1] as Timestamp?)?.toLocalDateTime()?.toLocalDate()),
                    transferenciaId = (it[2] as BigInteger),
                    identificadorTransferencia = (it[3] as BigInteger)
                )
            }.toList()
        }
    }
}