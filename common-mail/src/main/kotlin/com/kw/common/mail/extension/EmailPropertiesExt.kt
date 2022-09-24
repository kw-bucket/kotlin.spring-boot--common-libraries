package com.kw.common.mail.extension

import com.kw.common.mail.config.properties.EmailProperties
import com.kw.common.mail.dto.notification.EmailNotificationRequest
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
