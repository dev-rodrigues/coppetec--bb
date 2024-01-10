package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.port

import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.ErroTransferenciaEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.TransferenciaEnviadaEntity
import java.math.BigInteger

interface TransferenciaRepository {
    fun findAllByLoteId(loteId: BigInteger): List<TransferenciaEnviadaEntity>
    fun findErroBy(transferenciaId: BigInteger): List<ErroTransferenciaEntity>
}