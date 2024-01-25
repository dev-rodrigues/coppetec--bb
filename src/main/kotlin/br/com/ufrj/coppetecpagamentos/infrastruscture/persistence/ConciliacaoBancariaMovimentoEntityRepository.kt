package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.ConciliacaoBancariaMovimentoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigInteger

interface ConciliacaoBancariaMovimentoEntityRepository
    : JpaRepository<ConciliacaoBancariaMovimentoEntity, BigInteger>