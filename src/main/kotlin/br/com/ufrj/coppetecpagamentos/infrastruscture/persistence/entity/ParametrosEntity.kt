package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(catalog = "COPPETEC", schema = "bancoDoBrasil_remessa", name = "parametros")
data class ParametrosEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int? = null,
    @Column(name = "envio")
    var enviarTransferencia: Boolean? = null,
    @Column(name = "consultaPasso1")
    var consultarPrioridade: Boolean? = null,
    @Column(name = "consultaPasso2")
    var consultarSemPrioridade: Boolean? = null,
)