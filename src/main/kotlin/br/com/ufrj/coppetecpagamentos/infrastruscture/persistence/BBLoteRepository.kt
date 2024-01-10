package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBLoteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigInteger

interface BBLoteRepository : JpaRepository<BBLoteEntity, BigInteger> {
    @Query(
        "SELECT l FROM BBLoteEntity l " +
                "WHERE l.estadoRequisicao IS NULL " +
                "OR (l.estadoRequisicao NOT IN (6, 7) AND l.id IN (SELECT t.lote FROM BBTransferenciaEntity t WHERE t.estadoPagamento IS NULL)) " +
                "OR l.estadoRequisicao IN (1, 2, 4, 5, 8, 9, 10)"
    )
    fun findAllByCustomQuery(): List<BBLoteEntity>
}