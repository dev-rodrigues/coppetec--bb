package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.ParametrosEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ParametrosEntityRepository : JpaRepository<ParametrosEntity, Int>