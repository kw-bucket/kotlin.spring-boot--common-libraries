package com.kw.common.starter.service.logger

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kw.common.starter.interceptor.wrapper.RequestWrapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class HttpLoggerService {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun logRequest(request: HttpServletRequest) {
        request as RequestWrapper

        logger.info(
            """
            HTTP Request :: {} {}
                - Query Params: {}
                - Headers: {}
                - Body: {}
            """.trimIndent(),
            request.method, request.requestURI,
            request.parameterNames.toList().associateWith { request.getParameterValues(it).toList() },
            request.headerNames.toList().associateWith { request.getHeaders(it).toList() },
            request.body,
        )
    }

    fun logResponse(request: HttpServletRequest, response: HttpServletResponse, body: Any?) {
        logger.info(
            """
            HTTP Response :: {} {}
                - Headers: {}
                - Body: {}
            """.trimIndent(),
            request.method, request.requestURI,
            response.headerNames.toList().associateWith { request.getHeaders(it).toList() },
            jacksonObjectMapper().writeValueAsString(body),
        )
    }
}
