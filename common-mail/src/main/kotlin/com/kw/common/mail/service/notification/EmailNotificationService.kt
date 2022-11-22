package com.kw.common.mail.service.notification

import com.kw.common.mail.dto.notification.EmailNotificationRequest
import com.kw.common.mail.service.api.NotificationApiService
import com.kw.common.starter.extension.apioutcome.peekAppStatus
import com.kw.common.starter.extension.apioutcome.peekError
import com.kw.common.starter.manager.ThreadPoolManager
import com.kw.common.starter.service.api.ApiOutcome
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EmailNotificationService(private val notificationApiService: NotificationApiService) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun sendEmail(request: EmailNotificationRequest) = callEmailNotificationApi(request)

    fun sendEmailAsync(request: EmailNotificationRequest) {
        val pool = ThreadPoolManager.initFixedThreadPoolTaskExecutor(nThreads = 1, threadNamePrefix = "Thd-EmailAsync-")

        pool.execute {
            val result = when (val response = callEmailNotificationApi(request)) {
                is ApiOutcome.Success,
                is ApiOutcome.Failure -> response.peekAppStatus()
                is ApiOutcome.Error -> response.peekError()
            }

            logger.info("Send asynchronous email notification - Subject[{}] - Result[{}]", request.subject, result)
        }

        pool.shutdown()
    }

    private fun callEmailNotificationApi(request: EmailNotificationRequest) = notificationApiService.sendEmail(request)
}
