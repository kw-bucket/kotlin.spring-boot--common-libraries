package com.kw.starter.common.exception

import com.kw.starter.common.constant.AppStatus
import org.springframework.http.HttpStatus

class AppException(
    val httpStatus: HttpStatus,
    val appStatus: AppStatus,
    val description: String? = null,
    override val cause: Throwable? = null,
) : RuntimeException(cause)
