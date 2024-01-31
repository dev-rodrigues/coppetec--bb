package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBTransferenciaErroEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigInteger

interface BBTransferenciaErroEntityRepository : JpaRepository<BBTransferenciaErroEntity, BigInteger>