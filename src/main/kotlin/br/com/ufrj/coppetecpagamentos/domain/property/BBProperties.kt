package br.com.ufrj.coppetecpagamentos.domain.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "bb")
data class BBProperties(
    val autenticacao: AutenticacaoProperties,
    val endpoints: EndpointsProperties
)


data class AutenticacaoProperties(
    val autorizacao: String,
    val chaveAplicacao: String,
    val ambiente: String
)

data class EndpointsProperties(
    val baseUrl: String,
    val autenticar: String,
    val buscarSolicitacao: String,
    val buscarTransferencia: String,
    val consultar: String,
    val transferir: String,
    val lotesTransferencias: String,
    val liberarLote: String,
    val extrato: String,
)