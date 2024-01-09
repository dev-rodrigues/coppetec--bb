package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import br.com.ufrj.coppetecpagamentos.domain.common.formatarData
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBConsultaTransferenciaResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.Transferencia
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(catalog = "COPPETEC", schema = "bancoDoBrasil_remessa", name = "transferencia")
data class BBTransferenciaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: BigInteger? = null,
    var lote: BigInteger? = null,
    var lancamento: BigInteger? = null,
    var estadoPagamento: Int? = null,
    var identificadorTransferencia: BigInteger? = null,
    var tipoCredito: BigInteger? = null,
    var inicioCartaoCredito: BigInteger? = null,
    var fimCartaoCredito: BigInteger? = null,
    var dataPagamento: LocalDateTime? = null,
    var valorPagamento: BigDecimal? = null,
    var documentoDebito: BigInteger? = null,
    var codigoAutenticacaoPagamento: String? = null,
    var indicadorAceite: String? = null,
    var codigoFinalidadeDOC: String? = null,
    var codigoFInalidadeTED: String? = null,
    var numeroDepositoJudicial: String? = null,
    var documentoCredito: BigInteger? = null,
    var cnpjBeneficiario: String? = null,
    var cpfBeneficiario: String? = null,
    var contaPagamentoCredito: String? = null,
    var digitoVerificadorContaCorrente: String? = null,
    var contaCorrenteCredito: String? = null,
    var agenciaCredito: String? = null,
    var numeroISPB: String? = null,
    var numeroCOMPE: String? = null,
) {

    fun atualizarRegistro(transferenciaBB: Transferencia): BBTransferenciaEntity {

        val logger = LoggerFactory.getLogger(this.javaClass)
        logger.info("RESPOSTA TRANSFERENCIA BANCO DO BRASIL: {}", transferenciaBB)

        this.estadoPagamento = null
        this.identificadorTransferencia = transferenciaBB.identificadorTransferencia
        this.tipoCredito = transferenciaBB.tipoCredito
        this.inicioCartaoCredito = null
        this.fimCartaoCredito = null
        this.dataPagamento = formatarData(transferenciaBB.dataTransferencia!!)
        this.valorPagamento = transferenciaBB.valorTransferencia
        this.documentoDebito = transferenciaBB.documentoDebito
        this.codigoAutenticacaoPagamento = null
        this.indicadorAceite = transferenciaBB.indicadorAceite
        this.codigoFinalidadeDOC = transferenciaBB.codigoFinalidadeDOC
        this.codigoFInalidadeTED = transferenciaBB.codigoFinalidadeTED
        this.numeroDepositoJudicial = transferenciaBB.numeroDepositoJudicial
        this.documentoCredito = transferenciaBB.documentoCredito
        this.cnpjBeneficiario = transferenciaBB.cnpjBeneficiario?.toString()
        this.cpfBeneficiario = transferenciaBB.cpfBeneficiario?.toString()
        this.contaPagamentoCredito = transferenciaBB.contaPagamentoCredito
        this.digitoVerificadorContaCorrente = transferenciaBB.digitoVerificadorContaCorrente
        this.contaCorrenteCredito = transferenciaBB.contaCorrenteCredito?.toString()
        this.agenciaCredito = transferenciaBB.agenciaCredito?.toString()
        this.numeroISPB = transferenciaBB.numeroISPB?.toString()
        this.numeroCOMPE = transferenciaBB.numeroCOMPE?.toString()

        logger.info("TRANSFERENCIA DATABASE ATUALIZADA: {}", this)

        return this
    }

    fun atualizarTransferenciaComConsulta(
        bbTransferencia: BBConsultaTransferenciaResponseDto,
        estadoPagamento: BBEstadoTransferenciaEntity
    ):BBTransferenciaEntity {
        this.estadoPagamento = estadoPagamento.id
        this.inicioCartaoCredito = bbTransferencia.inicioCartaoCredito
        this.fimCartaoCredito = bbTransferencia.fimCartaoCredito
        this.codigoAutenticacaoPagamento = bbTransferencia.codigoAutenticacaoPagamento
        return this
    }

    companion object {
        fun criarRegistroTransferencia(idLote: BigInteger, documentoDebito: BigInteger): BBTransferenciaEntity {
            return BBTransferenciaEntity(
                lote = idLote,
                lancamento = documentoDebito,
            )
        }
    }

}