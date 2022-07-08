package com.kw.starter.mailable.service.notification

import com.kw.starter.common.manager.ThreadPoolManager
import com.kw.starter.common.service.api.ApiOutcome
import com.kw.starter.common.service.api.peekAppStatus
import com.kw.starter.common.service.api.peekError
import com.kw.starter.mailable.dto.notification.EmailNotificationRequest
import com.kw.starter.mailable.service.api.NotificationApiService
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
