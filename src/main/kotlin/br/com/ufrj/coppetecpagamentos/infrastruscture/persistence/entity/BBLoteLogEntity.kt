package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.math.BigInteger
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

@Entity
@Table(catalog = "COPPETEC", schema = "bancoDoBrasil_remessa", name = "loteLog")
class BBLoteLogEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private var id: BigInteger? = null

    private lateinit var lote: BigInteger

    private var httpCodigo: Int? = null

    @Column(name = "codigo_erro")
    private var codigoErro: String? = null

    @Column(name = "mensagem_erro")
    private var mensagemErro: String? = null

    private var ocorrencia: String? = null

    @Column(name = "payload")
    private var payload: String? = null


    constructor() {
        // Default constructor
    }

    constructor(
        lote: BigInteger,
        httpCodigo: Int?,
        codigoErro: String?,
        mensagemErro: String?,
        ocorrencia: String?,
        payload: String
    ) {
        this.id = null
        this.lote = lote
        this.httpCodigo = httpCodigo
        this.codigoErro = codigoErro
        this.mensagemErro = mensagemErro
        this.ocorrencia = ocorrencia
        this.payload = payload
    }
}