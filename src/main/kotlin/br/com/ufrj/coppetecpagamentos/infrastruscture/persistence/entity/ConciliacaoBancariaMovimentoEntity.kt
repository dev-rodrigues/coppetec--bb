package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(
    catalog = "COPPETEC",
    schema = "conciliacaoBancaria",
    name = "movimento_Teste"
)
class ConciliacaoBancariaMovimentoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: BigInteger? = null,

    @Column(name = "idImportacao")
    var idImportacao: BigInteger? = null,

    @Column(name = "numeroSequencialExtrato")
    var numeroSequencialExtrato: BigInteger? = null,

    @Column(name = "numeroSequencialNoArquivo")
    var numeroSequencialNoArquivo: BigInteger? = null,

    @Column(name = "numeroSequencialNoLote")
    var numeroSequencialNoLote: Int? = null,

    @Column(name = "banco")
    var banco: String? = null,

    @Column(name = "agencia")
    var agencia: String? = null,

    @Column(name = "agenciaDV")
    var agenciaDV: String? = null,

    @Column(name = "contaCorrente")
    var contaCorrente: String? = null,

    @Column(name = "contaCorrenteDV")
    var contaCorrenteDV: String? = null,

    @Column(name = "contaCorrenteSIC")
    var contaCorrenteSIC: String? = null,

    @Column(name = "contaCorrenteDescricao")
    var contaCorrenteDescricao: String? = null,

    @Column(name = "movimentoData")
    var movimentoData: LocalDateTime? = null,

    @Column(name = "movimentoDataContabil")
    var movimentoDataContabil: LocalDateTime? = null,

    @Column(name = "movimentoTipo")
    var movimentoTipo: String? = null,

    @Column(name = "movimentoValor")
    var movimentoValor: BigDecimal? = null,

    @Column(name = "movimentoSaldo")
    var movimentoSaldo: BigDecimal? = null,

    @Column(name = "posicaoSaldo", length = 1)
    var posicaoSaldo: String? = null,

    @Column(name = "natureza", length = 3)
    var natureza: String? = null,

    @Column(name = "complementoTipo", length = 2)
    var complementoTipo: String? = null,

    @Column(name = "complementoBancoOrigem", length = 3)
    var complementoBancoOrigem: String? = null,

    @Column(name = "complementoAgenciaOrigem", length = 5)
    var complementoAgenciaOrigem: String? = null,

    @Column(name = "complementoContaCorrenteOrigem", length = 20)
    var complementoContaCorrenteOrigem: String? = null,

    @Column(name = "complementoContaCorrenteDVOrigem", length = 1)
    var complementoContaCorrenteDVOrigem: String? = null,

    @Column(name = "complementoAlfa", length = 12)
    var complementoAlfa: String? = null,

    @Column(name = "isencaoCPMF", length = 1)
    var isencaoCPMF: String? = null,

    @Column(name = "movimentoCategoria", length = 3)
    var movimentoCategoria: String? = null,

    @Column(name = "codigoHistorico", length = 4)
    var codigoHistorico: String? = null,

    @Column(name = "descricaoHistorico", length = 255)
    var descricaoHistorico: String? = null,

    @Column(name = "documentoNumero", length = 39)
    var documentoNumero: String? = null,

    @Column(name = "somatorioValoresADebito")
    var somatorioValoresADebito: BigDecimal? = null,

    @Column(name = "somatorioValoresACredito")
    var somatorioValoresACredito: BigDecimal? = null,

    @Column(name = "numeroLancamentos")
    var numeroLancamentos: Int? = null,

    @Column(name = "numeroCpfCnpjContrapartida", length = 18)
    var numeroCpfCnpjContrapartida: String? = null,

    @Column(name = "indicadorTipoPessoaContrapartida", length = 1)
    var indicadorTipoPessoaContrapartida: String? = null,
)