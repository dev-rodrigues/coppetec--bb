package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LogLoteEnviadoEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LoteEnviadoEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.LoteEnviadoEntityPaginated
import java.math.BigInteger

interface LoteRepository {
    fun findAll(): List<LoteEnviadoEntity>
    fun findPaginated(page: Int, size: Int): LoteEnviadoEntityPaginated
    fun findLogBy(loteId: BigInteger): List<LogLoteEnviadoEntity>
}