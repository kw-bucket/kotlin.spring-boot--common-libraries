package com.kw.starter.common.manager

import com.kw.starter.common.config.properties.ThreadPoolProperties
import com.kw.starter.common.decorator.MdcTaskDecorator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

object ThreadPoolManager {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun initThreadPoolTaskExecutor(properties: ThreadPoolProperties): ThreadPoolTaskExecutor =
        ThreadPoolTaskExecutor()
            .apply {
                setThreadNamePrefix(properties.threadNamePrefix)
                setQueueCapacity(properties.capacity)

                keepAliveSeconds = properties.keepAliveTimeSec
                corePoolSize = properties.pooling.core
                maxPoolSize = properties.pooling.max

                setTaskDecorator(MdcTaskDecorator())
                afterPropertiesSet()
            }
            .also {
                logger.trace(
                    """
                    Configure Thread Pool Executor:
                        - Thread-Name-Prefix[{}]
                        - Queue-Capacity[{}]
                        - Core-Pool-Size[{}]
                        - Max-Pool-Size[{}]
                    """.trimIndent(),
                    it.threadNamePrefix,
                    properties.capacity,
                    it.corePoolSize,
                    it.maxPoolSize
                )
            }

    fun initFixedThreadPoolTaskExecutor(
        nThreads: Int,
        capacity: Int = Int.MAX_VALUE,
        keepAliveTimeSec: Int = 0,
        threadNamePrefix: String = "Thd-",
    ): ThreadPoolTaskExecutor =
        ThreadPoolTaskExecutor()
            .apply {
                setThreadNamePrefix(threadNamePrefix)
                setQueueCapacity(capacity)

                keepAliveSeconds = keepAliveTimeSec
                corePoolSize = nThreads
                maxPoolSize = nThreads

                setTaskDecorator(MdcTaskDecorator())
                afterPropertiesSet()
            }
            .also {
                logger.trace(
                    """
                    Configure Fixed Thread Pool:
                        - Core-Pool-Size[{}]
                        - Max-Pool-Size[{}]
                    """.trimIndent(),
                    it.corePoolSize,
                    it.maxPoolSize
                )
            }

    fun initFixedThreadPoolExecutor(
        nThreads: Int,
        capacity: Int = Int.MAX_VALUE,
        keepAliveTimeSec: Int = 0,
    ): ThreadPoolExecutor =
        ThreadPoolExecutor(
            nThreads,
            nThreads,
            keepAliveTimeSec.seconds.inWholeMilliseconds,
            TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(capacity),
        )
}
