package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import javax.persistence.*

@Entity
@Table(catalog = "COPPETEC", schema = "bancoDoBrasil_remessa", name = "transferenciaEstado")
class BBEstadoTransferenciaEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
    var estadoPagamento: String? = null,
    var descricao: String? = null,
    var ok: Boolean? = null
)