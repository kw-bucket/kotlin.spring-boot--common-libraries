package com.kw.common.starter.controller.advice

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.kw.common.starter.constant.AppStatus
import com.kw.common.starter.dto.AppResponse
import com.kw.common.starter.exception.AppException
import com.kw.common.starter.extension.toSnakeCase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class AppControllerAdvice {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${common.application-code:APP}")
    private val applicationCode: String = "APP"

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<AppResponse<Void>> =
        buildResponse(
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
            status = ExceptionStatus.E5000,
            description = ex.message,
        )
            .also { logger.error("Exception: {}", ex.stackTraceToString()) }

    @ExceptionHandler(
        value = [
            HttpMessageNotReadableException::class,
            MethodArgumentNotValidException::class,
            MissingServletRequestParameterException::class,
            MethodArgumentTypeMismatchException::class,
            InvalidFormatException::class,
        ]
    )
    fun handleInvalidRequest(ex: Exception): ResponseEntity<AppResponse<Void>> {
        logger.error("Invalid Request: {}", ex.localizedMessage)

        val description: String? = when (ex) {
            is MethodArgumentNotValidException -> {
                ex.fieldError.let { "${it?.field?.toSnakeCase()} - ${it?.defaultMessage}" }
            }
            is HttpMessageNotReadableException -> "Invalid Request Format"

            else -> null
        }

        return buildResponse(HttpStatus.BAD_REQUEST, ExceptionStatus.E4000, description)
    }

    @ExceptionHandler(AppException::class)
    fun handleAppException(ex: AppException): ResponseEntity<AppResponse<Void>> =
        buildResponse(ex.httpStatus, ex.appStatus, ex.description)

    private fun buildResponse(
        httpStatus: HttpStatus,
        status: AppStatus,
        description: String? = null,
    ): ResponseEntity<AppResponse<Void>> =
        AppResponse
            .fromAppStatus<Void>(status, description)
            .let { ResponseEntity.status(httpStatus).body(it) }

    private fun buildResponse(
        httpStatus: HttpStatus,
        status: ExceptionStatus,
        description: String? = null,
    ): ResponseEntity<AppResponse<Void>> {
        val code = "${applicationCode.toUpperCase()}${status.code}"
        val appResponse: AppResponse<Void> = AppResponse.fromCustomStatus(
            code = code,
            message = status.message,
            description = description ?: status.description,
        )

        return ResponseEntity.status(httpStatus).body(appResponse)
    }
}

private enum class ExceptionStatus(
    override val code: String,
    override val message: String,
    override val description: String,
) : AppStatus {
    E4000("4000", "Bad Request", "Bad Request"),
    E5000("5000", "Internal Server Error", "Internal Server Error"),
}
