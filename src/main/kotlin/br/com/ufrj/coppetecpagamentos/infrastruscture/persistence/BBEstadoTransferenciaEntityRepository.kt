package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBEstadoTransferenciaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigInteger

interface BBEstadoTransferenciaEntityRepository : JpaRepository<BBEstadoTransferenciaEntity, BigInteger> {

    @Query("SELECT b FROM BBEstadoTransferenciaEntity b WHERE LOWER(b.estadoPagamento) = LOWER(:estadoPagamento)")
    fun findByEstadoPagamentoIgnoreCase(@Param("estadoPagamento") estadoPagamento: String): BBEstadoTransferenciaEntity

}