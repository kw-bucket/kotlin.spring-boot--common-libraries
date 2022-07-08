package com.kw.starter.sftp.config.properties

data class SftpProperties(
    val host: String,
    val port: Int = 22,
    val username: String,
    val password: String,
    val prefix: String,
    val timeout: Int? = null,
    val session: SftpSessionConfig = SftpSessionConfig(),
) {
    data class SftpSessionConfig(
        val strictHostKeyChecking: String? = null,
        val preferredAuthentications: String? = null,
    )
}
