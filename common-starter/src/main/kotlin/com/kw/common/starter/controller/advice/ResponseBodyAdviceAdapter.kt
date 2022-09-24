package com.kw.common.starter.controller.advice

import com.kw.common.starter.service.logger.HttpLoggerService
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@ControllerAdvice
class ResponseBodyAdviceAdapter : ResponseBodyAdvice<Any> {

    private val httpLoggerService = HttpLoggerService()

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean = true

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse,
    ): Any? {
        httpLoggerService.logResponse(
            request = (request as ServletServerHttpRequest).servletRequest,
            response = (response as ServletServerHttpResponse).servletResponse,
            body = body,
        )

        return body
    }
}
