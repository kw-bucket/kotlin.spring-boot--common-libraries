package com.kw.common.starter.exception

import com.kw.common.starter.constant.ApiOutputStatus
import org.springframework.http.HttpStatus

class AppException(
    val httpStatus: HttpStatus,
    val apiOutputStatus: ApiOutputStatus,
    val description: String? = null,
    override val cause: Throwable? = null,
) : RuntimeException(cause)
