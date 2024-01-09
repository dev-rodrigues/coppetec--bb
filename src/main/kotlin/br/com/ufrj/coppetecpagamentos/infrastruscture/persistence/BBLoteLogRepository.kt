package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBLoteLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigInteger

interface BBLoteLogRepository: JpaRepository<BBLoteLogEntity, BigInteger> {
}