package com.kw.starter.common.service.api

import com.kw.starter.common.dto.AppResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

sealed class ApiOutcome<out T> {
    abstract val httpStatus: HttpStatus
    abstract val httpHeaders: HttpHeaders?
    abstract val body: T?

    data class Success<out T> (
        override val httpStatus: HttpStatus,
        override val httpHeaders: HttpHeaders? = null,
        override val body: T? = null,
    ) : ApiOutcome<T>()

    data class Failure<out T> (
        override val httpStatus: HttpStatus,
        override val httpHeaders: HttpHeaders? = null,
        override val body: T? = null,
    ) : ApiOutcome<T>()

    data class Error(
        override val httpStatus: HttpStatus,
        override val httpHeaders: HttpHeaders? = null,
        override val body: Nothing? = null,

        val bodyAsString: String? = null,
        val cause: Throwable? = null,
    ) : ApiOutcome<Nothing>()
}

fun <T> ApiOutcome<AppResponse<T>>.peekAppStatus(): String {
    val code = this.body?.status?.code ?: this.httpStatus.value()
    val description = this.body?.status?.description ?: this.httpStatus.reasonPhrase

    return "$code <$description>"
}

fun ApiOutcome.Error.peekError(): String {
    val code = httpStatus.value()
    val description = httpStatus.reasonPhrase + cause?.let { "|${it.message}" }.orEmpty()

    return "$code <$description>"
}

fun <T> ApiOutcome<T>.peekHttpStatus(): String = "${this.httpStatus.value()} ${this.httpStatus.reasonPhrase}"
