package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBLoteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigInteger

interface BBLoteRepository : JpaRepository<BBLoteEntity, BigInteger> {
    @Query(
        "SELECT l FROM BBLoteEntity l " +
                "WHERE (l.canceladoEm IS NULL)" +
                "AND (l.estadoRequisicao IN :estadosRequisicao) " +
                "OR (l.id in (" +
                "               SELECT t.lote " +
                "               FROM BBTransferenciaEntity t " +
                "               WHERE t.estadoPagamento IS NULL) " +
                "   ) " +
                "ORDER BY l.id DESC"
    )
    fun findLotesByEstadoRequisicao(
        @Param("estadosRequisicao") estados: List<Int> = listOf(1, 2, 8, 10)
    ): List<BBLoteEntity>

}