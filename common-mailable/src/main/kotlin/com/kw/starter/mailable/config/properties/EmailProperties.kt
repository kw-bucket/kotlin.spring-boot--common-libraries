package com.kw.starter.mailable.config.properties

data class EmailProperties(
    val from: String? = null,
    val to: String? = null,
    val cc: String? = null,
    val bcc: String? = null,
    val subject: String? = null,
    val bodyTemplate: String? = null,
)
