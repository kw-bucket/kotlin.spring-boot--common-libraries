package com.kw.common.starter.extension.apioutcome

import com.kw.common.starter.dto.ApiOutput
import com.kw.common.starter.service.api.ApiResponse

fun <T> ApiResponse<ApiOutput<T>>.peekAppStatus(): String {
    val code = this.body?.status?.code ?: this.httpStatus.value()
    val description = this.body?.status?.description ?: this.httpStatus.reasonPhrase

    return "$code <$description>"
}

fun ApiResponse.Error.peekError(): String {
    val code = httpStatus.value()
    val description = httpStatus.reasonPhrase + cause?.let { "|${it.message}" }.orEmpty()

    return "$code <$description>"
}

fun <T> ApiResponse<T>.peekHttpStatus(): String = "${this.httpStatus.value()} ${this.httpStatus.reasonPhrase}"
