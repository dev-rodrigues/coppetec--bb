package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBConsultaLoteResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBLoteResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBTransferenciaResponseDto
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(catalog = "COPPETEC", schema = "bancoDoBrasil_remessa", name = "lote")
class BBLoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: BigInteger? = null
    private var dataHora: LocalDateTime? = null
    private var estadoRequisicao: Int? = null
    private var quantidadeTransferencias: Int? = null
    private var valorTransferencias: BigDecimal? = null
    private var quantidadeTransferenciasValidas: Int? = null
    private var valorTransferenciasValidas: BigDecimal? = null

    constructor() {
        // Default constructor
    }

    constructor(dataHora: LocalDateTime) {
        this.id = null
        this.dataHora = dataHora
    }

    fun atualizarBBLoteEntityComResposta(resposta: BBTransferenciaResponseDto): BBLoteEntity {
        val logger = LoggerFactory.getLogger(BBLoteEntity::class.java)

        this.estadoRequisicao = resposta.estadoRequisicao
        this.quantidadeTransferencias = resposta.quantidadeTransferencias
        this.valorTransferencias = resposta.valorTransferencias
        this.quantidadeTransferenciasValidas = resposta.quantidadeTransferenciasValidas
        this.valorTransferenciasValidas = resposta.valorTransferenciasValidas

        logger.info("STEP 1: LOTE ATUALIZADO COM RESPOSTA {}", this.toString())
        return this;
    }

    fun atualizarBBLoteEntityComConsulta(loteConsultado: BBConsultaLoteResponseDto): BBLoteEntity {
        this.estadoRequisicao = loteConsultado.estadoRequisicao
        this.quantidadeTransferencias = loteConsultado.quantidadePagamentos
        this.valorTransferencias = loteConsultado.valorPagamentos
        this.quantidadeTransferenciasValidas = loteConsultado.quantidadePagamentosValidos
        this.valorTransferenciasValidas = loteConsultado.valorPagamentosValidos
        return this
    }

    override fun toString(): String {
        return "BBLoteEntity(id=$id, dataHora=$dataHora, estadoRequisicao=$estadoRequisicao)"
    }
}