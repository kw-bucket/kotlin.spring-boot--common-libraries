package com.kw.starter.sftp.service

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.SftpException
import com.kw.starter.sftp.config.properties.SftpProperties
import com.kw.starter.sftp.dto.SftpRequest
import com.kw.starter.sftp.extension.close
import com.kw.starter.sftp.manager.SftpConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

abstract class SftpService(private val sftpProperties: SftpProperties) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun executeBulk(
        requests: List<SftpRequest>,
        concurrent: Int = requests.size,
        _channel: ChannelSftp? = null,
    ) {
        val channel = _channel ?: SftpConnectionManager.openChannel(properties = sftpProperties)
        val semaphore = Semaphore(concurrent)
        val mdcContext = MDCContext()

        runBlocking(mdcContext) {
            requests.map { request ->
                launch(Dispatchers.IO) { semaphore.withPermit { execute(request = request, _channel = channel) } }
            }.joinAll()
        }

        if (_channel == null) channel?.close()

        logger.info("[SFTP] Execute Bulk Done! - Size: {}", requests.size)
    }

    fun execute(request: SftpRequest, _channel: ChannelSftp? = null) {
        val channel = _channel ?: SftpConnectionManager.openChannel(properties = sftpProperties)

        channel
            ?.also { chn ->
                when (request.command) {
                    SftpRequest.Command.Upload ->
                        upload(chn, request.localPath.toString(), request.remotePath.toString())

                    SftpRequest.Command.Download ->
                        download(chn, request.localPath.toString(), request.remotePath.toString())
                }

                if (_channel == null) chn.close()
            }
    }

    fun createDirectories(path: Path, _channel: ChannelSftp? = null): Path? {
        val channel = _channel ?: SftpConnectionManager.openChannel(properties = sftpProperties)

        return channel?.also { chn ->
            if (path.startsWith("/")) chn.cd("/")

            path.toString()
                .split("/")
                .filterNot { it.isEmpty() }
                .forEach { dir ->
                    try {
                        chn.cd(dir)
                    } catch (ex: SftpException) {
                        chn.mkdir(dir)
                        chn.cd(dir)
                    }
                }

            logger.debug("[SFTP] Create directories: {} -> done!", path.toString())

            if (_channel == null) chn.close()
        }
            ?.let { path }
    }

    private fun upload(channel: ChannelSftp, localPath: String, remotePath: String) {
        try {
            channel.put(localPath, remotePath)
                .also {
                    logger.debug(
                        """
                            [SFTP] Upload Completed
                                - Local Path: {}
                                - Remote Path: {}
                        """.trimIndent(),
                        localPath, remotePath,
                    )
                }
        } catch (ex: SftpException) {
            logger.error(
                """
                    [SFTP] Upload Failure
                        - Local Path: {}
                        - Remote Path: {}
                        - Message: {}
                """.trimIndent(),
                localPath, remotePath, ex.message,
            )
            logger.error(ex.stackTraceToString())
        }
    }

    private fun download(channel: ChannelSftp, localPath: String, remotePath: String) {
        try {
            channel.get(localPath, remotePath)
                .also {
                    logger.debug(
                        """
                            [SFTP] Download Completed
                                - Local Path: {}
                                - Remote Path: {}
                        """.trimIndent(),
                        localPath, remotePath,
                    )
                }
        } catch (ex: SftpException) {
            logger.error(
                """
                    [SFTP] Download Failure
                        - Local Path: {}
                        - Remote Path: {}
                        - Message: {}
                """.trimIndent(),
                localPath, remotePath, ex.message,
            )
            logger.error(ex.stackTraceToString())
        }
    }
}
