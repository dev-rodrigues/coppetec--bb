package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBTransferenciaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigInteger

interface BBTransferenciaEntityRepository : JpaRepository<BBTransferenciaEntity, BigInteger> {
    fun findByLoteAndLancamento(lote: BigInteger, lancamento: BigInteger): BBTransferenciaEntity
    fun findAllByLote(lote: BigInteger): List<BBTransferenciaEntity>
    fun findAllByEstadoPagamentoIsNullAndIdentificadorTransferenciaIsNotNullOrEstadoPagamentoIn(ids: List<Int>): List<BBTransferenciaEntity>
}