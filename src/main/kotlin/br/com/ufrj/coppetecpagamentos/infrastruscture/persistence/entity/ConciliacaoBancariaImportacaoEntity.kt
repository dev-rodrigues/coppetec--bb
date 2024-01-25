package br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity

import java.math.BigInteger
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(
    catalog = "COPPETEC",
    schema = "conciliacaoBancaria",
    name = "importacao_Teste"
)
class ConciliacaoBancariaImportacaoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: BigInteger? = null,

    var idLayOut: BigInteger? = null,

    var idDocumento: BigInteger? = null,

    var bancoOrigem: String? = null,

    var arquivoNome: String? = null,

    var arquivoGeracaoDataHora: LocalDateTime? = null,

    var arquivoNumeroSequencial: BigInteger? = null,

    var arquivoNumeroVersaoLayOut: String? = null,

    var qtdLotes: Int? = null,

    var qtdRegistros: Int? = null,

    var qtdContas: Int? = null,

    var dataHora: LocalDateTime? = null,

    var idUsuario: BigInteger? = null,

    var consultaAgencia: String? = null,

    var consultaContaCorrente: String? = null,

    var consultaPeriodoDe: LocalDateTime? = null,

    var consultaPeriodoAte: LocalDateTime? = null,
)