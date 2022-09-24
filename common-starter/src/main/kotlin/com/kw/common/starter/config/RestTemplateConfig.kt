package com.kw.common.starter.config

import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${common.rest-template.connection-request-timeout:30000}")
    private val connectionRequestTimeout: Int = 30_000
    @Value("\${common.rest-template.connect-timeout:30000}")
    private val connectTimeout: Int = 30_000
    @Value("\${common.rest-template.socket-timeout:30000}")
    private val socketTimeout: Int = 30_000

    @Value("\${common.rest-template.pooling.connection-limit:100}")
    private val connectionLimit: Int = 100
    @Value("\${common.rest-template.pooling.max-connection-per-route:50}")
    private val maxConnectionPerRoute: Int = 50

    @Bean
    fun connectionManager(): PoolingHttpClientConnectionManager =
        PoolingHttpClientConnectionManager()
            .apply {
                maxTotal = connectionLimit
                defaultMaxPerRoute = maxConnectionPerRoute
            }
            .also {
                logger.debug(
                    """
                    RestTemplate Pooling Connection Config:
                        - Max-Connections-In-Pool[{}]
                        - Max-Connections-Per-Route[{}]
                    """.trimIndent(),
                    it.maxTotal,
                    it.defaultMaxPerRoute,
                )
            }

    @Bean
    fun requestConfig(): RequestConfig =
        RequestConfig
            .custom()
            .apply {
                setConnectionRequestTimeout(connectionRequestTimeout)
                setConnectTimeout(connectTimeout)
                setSocketTimeout(socketTimeout)
            }
            .build()
            .also {
                logger.trace(
                    """
                    RestTemplate Request Config:
                        - Connection-Request-Timeout[{}ms]
                        - Connect-Timeout[{}ms]
                        - Socket-Timeout[{}ms]
                    """.trimIndent(),
                    it.connectionRequestTimeout,
                    it.connectTimeout,
                    it.socketTimeout
                )
            }

    @Bean
    fun httpClient(
        connectionManager: PoolingHttpClientConnectionManager,
        requestConfig: RequestConfig,
    ): CloseableHttpClient =
        HttpClientBuilder
            .create()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build()

    @Bean
    fun clientHttpRequestFactory(httpClient: HttpClient): HttpComponentsClientHttpRequestFactory =
        HttpComponentsClientHttpRequestFactory()
            .apply { setHttpClient(httpClient) }

    @Bean
    fun restTemplate(clientHttpRequestFactory: HttpComponentsClientHttpRequestFactory): RestTemplate =
        RestTemplate(clientHttpRequestFactory)
}
