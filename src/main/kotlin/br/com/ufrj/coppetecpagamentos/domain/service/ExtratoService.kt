package br.com.ufrj.coppetecpagamentos.domain.service

import br.com.ufrj.coppetecpagamentos.domain.common.formatarData
import br.com.ufrj.coppetecpagamentos.domain.model.API
import br.com.ufrj.coppetecpagamentos.domain.model.CreateLogRequestDto
import br.com.ufrj.coppetecpagamentos.domain.util.ContaUtil
import br.com.ufrj.coppetecpagamentos.domain.util.DateUtil
import br.com.ufrj.coppetecpagamentos.infrastruscture.client.LogClient
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.dto.response.BBConsultaExtratoResponseDto
import br.com.ufrj.coppetecpagamentos.infrastruscture.http.port.BBPort
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.ConciliacaoBancariaImportacaoEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.ConciliacaoBancariaMovimentoEntityRepository
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.BBContasAtivas
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.ConciliacaoBancariaImportacaoEntity
import br.com.ufrj.coppetecpagamentos.infrastruscture.persistence.entity.ConciliacaoBancariaMovimentoEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallbackWithoutResult
import org.springframework.transaction.support.TransactionTemplate
import java.math.BigInteger
import java.math.BigInteger.ZERO
import java.time.LocalDateTime
import javax.transaction.Transactional

@Service
class ExtratoService(
    private val bbPort: BBPort,
    private val conciliacaoRepository: ConciliacaoBancariaImportacaoEntityRepository,
    private val movimentoEntityRepository: ConciliacaoBancariaMovimentoEntityRepository,
    private val transactionManager: PlatformTransactionManager,
    private val logClient: LogClient,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)


    fun getExtrato(
        agencia: String,
        conta: String,
        dataInicioSolicitacao: String,
        dataFimSolicitacao: String,
        headerBody: BigInteger,
    ): BBConsultaExtratoResponseDto? {

        val token = bbPort.autenticar(
            api = API.EXTRATO,
            header = headerBody
        ).body?.accessToken!!

        var nextPage = 1
        var extrato: BBConsultaExtratoResponseDto? = null

        while (nextPage != 0) {

            logClient.createLog(
                CreateLogRequestDto(
                    header = headerBody,
                    aplicacao = 1,
                    classe = this::class.java.simpleName,
                    metodo = "getExtrato",
                    parametros = "[$agencia, $conta, $dataInicioSolicitacao, $dataFimSolicitacao, $headerBody]",
                    usuarioCodigo = null,
                    usuarioNome = null,
                    criticalidade = 1,
                    servico = 1,
                    mensagemDeErro = "CONSULTANDO EXTRATO DE $agencia, $conta, $dataInicioSolicitacao, $dataFimSolicitacao",
                )
            )

            val response = bbPort.consultarExtrato(
                agencia = agencia,
                conta = conta,
                token = token,
                numeroPaginaSolicitacao = nextPage,
                dataInicioSolicitacao = dataInicioSolicitacao,
                dataFimSolicitacao = dataFimSolicitacao,
                headerBody = headerBody
            )

            logClient.createLog(
                CreateLogRequestDto(
                    header = headerBody,
                    aplicacao = 1,
                    classe = this::class.java.simpleName,
                    metodo = "getExtrato",
                    parametros = "[$agencia, $conta, $dataInicioSolicitacao, $dataFimSolicitacao, $headerBody]",
                    usuarioCodigo = null,
                    usuarioNome = null,
                    criticalidade = 1,
                    servico = 1,
                    mensagemDeErro = "EXTRATO DE $agencia, $conta, $dataInicioSolicitacao, $dataFimSolicitacao CONSULTADO COM SUCESSO: $response",
                )
            )

            if (extrato != null) {
                val aux = response.body!!
                val lancamento = aux.listaLancamento
                extrato.listaLancamento.addAll(lancamento)

                nextPage = aux.numeroPaginaProximo
            } else {
                extrato = response.body!!

                nextPage = extrato.numeroPaginaProximo
            }

        }

        return extrato
    }

    @Transactional
    fun register(
        consulta: BBContasAtivas,
        response: BBConsultaExtratoResponseDto?,
        headerBody: BigInteger,
    ) {
        val transactionTemplate = TransactionTemplate(transactionManager)

        transactionTemplate.execute(object : TransactionCallbackWithoutResult() {
            override fun doInTransactionWithoutResult(status: TransactionStatus) {

                try {
                    val importacao = conciliacaoRepository.save(
                        ConciliacaoBancariaImportacaoEntity(
                            id = null,
                            idLayOut = BigInteger.TWO,
                            idDocumento = null,
                            bancoOrigem = "001",
                            arquivoNome = "IMPORTED WITH API",
                            arquivoGeracaoDataHora = LocalDateTime.now(),
                            arquivoNumeroSequencial = BigInteger.ONE,
                            arquivoNumeroVersaoLayOut = "NaN",
                            qtdLotes = 1,
                            qtdRegistros = response?.listaLancamento?.size ?: 0,
                            qtdContas = 1,
                            dataHora = LocalDateTime.now(),
                            idUsuario = BigInteger.ONE,
                            consultaAgencia = consulta.agenciaSemDv?.replace(".", ""),
                            consultaContaCorrente = consulta.contaCorrente,
                            consultaPeriodoDe = formatarData(consulta.consultaPeriodoDe!!.toBigInteger()),
                            consultaPeriodoAte = formatarData(consulta.consultaPeriodoAte!!.toBigInteger())
                        )
                    )

                    if (response != null) {

                        val movimento = response.listaLancamento.map { lancamento ->

                            val dataMovimento = DateUtil.formatDate(lancamento.dataMovimento)
                            logger.info("DATA MOVIMENTO: $dataMovimento")

                            val dbDate = DateUtil.formatDate(consulta.consultaPeriodoAte)

                            if (dataMovimento != null && dataMovimento.dayOfWeek <= dbDate.dayOfWeek) {

                                ConciliacaoBancariaMovimentoEntity(
                                    id = null,
                                    idImportacao = importacao.id,
                                    numeroSequencialExtrato = ZERO,
                                    numeroSequencialNoArquivo = ZERO,
                                    numeroSequencialNoLote = lancamento.numeroLote,
                                    banco = "001",
                                    agencia = consulta.agenciaSemDv,
                                    agenciaDV = ContaUtil.getDV(consulta.agencia!!),
                                    contaCorrente = ContaUtil.getCC(consulta.contaCorrenteSemDv!!),
                                    contaCorrenteDV = ContaUtil.getDV(consulta.contaCorrente!!),
                                    contaCorrenteSIC = consulta.contaCorrente,
                                    contaCorrenteDescricao = null,
                                    movimentoData = DateUtil.formatDate(lancamento.dataLancamento),
                                    movimentoDataContabil = DateUtil.formatDate(lancamento.dataMovimento),
                                    movimentoTipo = lancamento.indicadorSinalLancamento,
                                    movimentoValor = lancamento.valorLancamento,
                                    movimentoSaldo = null,
                                    posicaoSaldo = null,
                                    natureza = null,
                                    complementoTipo = null,
                                    complementoBancoOrigem = lancamento.codigoBancoContrapartida.toString(),
                                    complementoAgenciaOrigem = lancamento.codigoAgenciaContrapartida.toString(),
                                    complementoContaCorrenteOrigem = lancamento.numeroContaContrapartida,
                                    complementoContaCorrenteDVOrigem = lancamento.textoDvContaContrapartida,
                                    complementoAlfa = lancamento.numeroContaContrapartida,
                                    isencaoCPMF = null,
                                    movimentoCategoria = null,
                                    codigoHistorico = lancamento.codigoHistorico.toString(),
                                    descricaoHistorico = lancamento.textoDescricaoHistorico,
                                    documentoNumero = lancamento.numeroDocumento.toString(),
                                    somatorioValoresADebito = null,
                                    somatorioValoresACredito = null,
                                    numeroLancamentos = 1,
                                    numeroCpfCnpjContrapartida = lancamento.numeroCpfCnpjContrapartida.toString(),
                                    indicadorTipoPessoaContrapartida = lancamento.indicadorTipoPessoaContrapartida,
                                )
                            } else {
                                null
                            }


                        }

                        movimentoEntityRepository.saveAll(
                            movimento.filterNotNull()
                        )
                    }

                } catch (e: Exception) {
                    logger.error("ERRO AO SALVAR EXTRATO: ${e.message}")
                }
            }
        })
    }
}