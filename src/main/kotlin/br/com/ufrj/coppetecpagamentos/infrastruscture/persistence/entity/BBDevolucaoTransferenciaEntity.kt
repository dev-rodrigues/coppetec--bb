package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(catalog = "COPPETEC", schema = "bancoDoBrasil_remessa", name = "transferenciaDevolucao")
class BBDevolucaoTransferenciaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
    var transferenciaId: BigInteger? = null,
    var codigoMotivo: BigInteger? = null,
    var dataDevolucao: LocalDateTime? = null,
    var valorDevolucao: BigDecimal? = null,
)