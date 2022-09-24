package com.kw.common.sftp.extension

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSchException

fun ChannelSftp.close() {
    try {
        val session = this.session

        this.disconnect()
        session.disconnect()
    } catch (ex: JSchException) {
        ex.printStackTrace()
    }
}
