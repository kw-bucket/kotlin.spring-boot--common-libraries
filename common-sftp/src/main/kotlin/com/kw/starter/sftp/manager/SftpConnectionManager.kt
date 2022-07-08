package com.kw.starter.sftp.manager

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.kw.starter.sftp.config.properties.SftpProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object SftpConnectionManager {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun openChannel(properties: SftpProperties): ChannelSftp? =
        try {
            createSession(properties)
                .let { it.openChannel("sftp") as ChannelSftp }
                .also { it.connect() }
        } catch (ex: JSchException) {
            logger.error("[SFTP] Error connecting to server: {}", ex.message)
            null
        }

    @Throws(JSchException::class)
    fun createSession(properties: SftpProperties): Session =
        JSch().getSession(properties.username, properties.host, properties.port)
            .apply {
                setPassword(properties.password)

                properties.timeout?.also { timeout = it }
                properties.session.strictHostKeyChecking?.also { setConfig("StrictHostKeyChecking", it) }
                properties.session.preferredAuthentications?.also { setConfig("PreferredAuthentications", it) }
            }
            .also { it.connect() }
}
