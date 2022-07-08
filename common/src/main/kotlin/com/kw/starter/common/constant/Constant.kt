package com.kw.starter.common.constant

object Constant {
    object Context {
        const val CORRELATION_ID = "Correlation-ID"
        const val TRACE_ID = "X-TraceId"
    }

    object Headers {
        const val CORRELATIONID = "correlationid"
        const val X_CORRELATION_ID = "x-correlation-id"
        const val TRACE_ID = "x-traceid"
        const val X_ACCESS_KEY = "x-access-key"

        val TRACES = listOf(X_CORRELATION_ID, TRACE_ID)
    }
}
