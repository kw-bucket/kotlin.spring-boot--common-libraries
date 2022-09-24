package com.kw.common.starter.exception

import com.kw.common.starter.constant.AppStatus
import org.springframework.http.HttpStatus

class AppException(
    val httpStatus: HttpStatus,
    val appStatus: AppStatus,
    val description: String? = null,
    override val cause: Throwable? = null,
) : RuntimeException(cause)
