package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.math.BigInteger
import javax.persistence.*

@Entity
@Table(catalog = "COPPETEC", schema = "bancoDoBrasil_remessa", name = "transferenciaErro")
data class BBTransferenciaErroEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: BigInteger? = null,
    var transferenciaId: BigInteger? = null,
    var codigoErro: Int? = null,
)