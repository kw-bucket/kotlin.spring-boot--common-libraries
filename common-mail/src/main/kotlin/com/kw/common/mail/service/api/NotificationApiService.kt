package com.kw.common.mail.service.api

import com.kw.common.mail.dto.notification.EmailNotificationRequest
import com.kw.common.mail.extension.emailnotificationrequest.asMap
import com.kw.common.starter.constant.Constant
import com.kw.common.starter.dto.ApiOutput
import com.kw.common.starter.service.api.ApiResponse
import com.kw.common.starter.service.api.ApiService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponents
import org.springframework.web.util.UriComponentsBuilder

@Service
class NotificationApiService(restTemplate: RestTemplate) : ApiService(restTemplate) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${api.notification.endpoints.send-email}")
    private val sendEmailUrl: String = "send-email"

    fun sendEmail(request: EmailNotificationRequest): ApiResponse<ApiOutput<Nothing>> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        headers.set(Constant.Headers.CORRELATIONID, MDC.get(Constant.Context.CORRELATION_ID))
        headers.set(Constant.Headers.X_CORRELATION_ID, MDC.get(Constant.Context.CORRELATION_ID))

        val uriComponents: UriComponents = UriComponentsBuilder.fromHttpUrl(sendEmailUrl).build()
        val responseType = object : ParameterizedTypeReference<ApiOutput<Nothing>>() {}
        val arguments = request.asMap()

        logger.debug(
            """
            Call Send Email Notification:
                - Endpoint: {}
                - Header: {}
                - Arguments: {}
            """.trimIndent(),
            uriComponents.toUriString(),
            headers,
            arguments,
        )

        return execute(HttpMethod.POST, uriComponents, HttpEntity(arguments, headers), responseType)
    }
}
