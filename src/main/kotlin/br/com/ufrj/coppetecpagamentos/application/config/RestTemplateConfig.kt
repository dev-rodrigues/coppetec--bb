package br.com.ufrj.coppetecpagamentos.application.config

import br.com.ufrj.coppetecpagamentos.domain.property.RestTemplateProperties
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContextBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.util.ResourceUtils
import org.springframework.web.client.RestTemplate
import java.io.FileInputStream
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException


@Configuration
class RestTemplateConfig(
    private val properties: RestTemplateProperties
) {

    @Bean
    @Throws(
        KeyStoreException::class,
        IOException::class,
        CertificateException::class,
        NoSuchAlgorithmException::class,
        UnrecoverableKeyException::class,
        KeyManagementException::class
    )
    fun restTemplate(): RestTemplate {
        val clientStore = KeyStore.getInstance(properties.keyStoreInstance)
        val file = ResourceUtils.getFile(properties.classPath)

        clientStore.load(FileInputStream(file), properties.password.toCharArray())

        val sslContextBuilder = SSLContextBuilder()
        sslContextBuilder.setProtocol(properties.protocol)
        sslContextBuilder.loadKeyMaterial(clientStore, properties.password.toCharArray())
        sslContextBuilder.loadTrustMaterial(TrustSelfSignedStrategy())

        val sslConnectionSocketFactory = SSLConnectionSocketFactory(sslContextBuilder.build())

        val httpClient: CloseableHttpClient =
            HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build()

        val requestFactory = HttpComponentsClientHttpRequestFactory(httpClient)
        requestFactory.setConnectTimeout(properties.connectionTimeout)
        requestFactory.setReadTimeout(properties.readTimeout)

        return RestTemplate(requestFactory)
    }
}