package com.kw.common.starter.extension.apioutcome

import com.kw.common.starter.dto.AppResponse
import com.kw.common.starter.service.api.ApiOutcome

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
