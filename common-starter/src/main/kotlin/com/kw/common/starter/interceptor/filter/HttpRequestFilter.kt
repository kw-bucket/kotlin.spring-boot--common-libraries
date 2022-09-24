package com.kw.common.starter.interceptor.filter

import com.kw.common.starter.constant.Constant
import com.kw.common.starter.interceptor.wrapper.RequestWrapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class HttpRequestFilter : OncePerRequestFilter() {

    @Value("\${common.application-code:APP}")
    private val applicationCode: String = "APP"

    private val disabledLoggingEndpoints = listOf("/health", "/info", "/metrics")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val ignore = disabledLoggingEndpoints.any { request.requestURI.endsWith(it) }
        if (ignore) {
            filterChain.doFilter(request, response)
            return
        }

        val wrappedRequest = RequestWrapper(request)
        val xCorrelationId = Constant.Headers.X_CORRELATION_ID

        Constant.Headers.TRACES.forEach { header ->
            request.getHeader(header)
                .let { if (header == xCorrelationId && it.isNullOrBlank()) initCorrelationId() else it }
                ?.also {
                    wrappedRequest.addHeader(header, it)
                    response.setHeader(header, wrappedRequest.getHeader(header))
                }
        }

        filterChain.doFilter(wrappedRequest, response)
    }

    fun initCorrelationId(): String =
        UUID.randomUUID().toString()
            .replace("-", "")
            .lowercase()
            .take(24)
            .let { "${applicationCode.lowercase()}-$it" }
}
