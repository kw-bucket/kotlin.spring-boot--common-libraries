package com.kw.starter.mailable.extension

import com.kw.starter.mailable.config.properties.EmailProperties
import com.kw.starter.mailable.dto.notification.EmailNotificationRequest
import java.io.File

fun EmailProperties.toRequest(
    subject: String? = null,
    body: String,
    attachments: List<File>? = null
): EmailNotificationRequest =
    EmailNotificationRequest(
        subject = subject ?: this.subject!!,
        from = this.from!!,
        to = this.to!!,
        cc = this.cc,
        bcc = this.bcc,
        body = body,
        files = attachments,
    )
