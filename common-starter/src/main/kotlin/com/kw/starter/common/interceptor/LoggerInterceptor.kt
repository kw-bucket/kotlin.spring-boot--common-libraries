package com.kw.starter.common.interceptor

import com.kw.starter.common.service.logger.HttpLoggerService
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LoggerInterceptor : HandlerInterceptor {

    private val httpLoggerService = HttpLoggerService()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        httpLoggerService.logRequest(request)

        return super.preHandle(request, response, handler)
    }
}
