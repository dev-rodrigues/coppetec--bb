package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBDevolucaoTransferenciaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigInteger

interface BBDevolucaoTransferenciaEntityRepository: JpaRepository<BBDevolucaoTransferenciaEntity, BigInteger> {
    fun findAllByTransferenciaId(transferenciaId: BigInteger): List<BBDevolucaoTransferenciaEntity>
}