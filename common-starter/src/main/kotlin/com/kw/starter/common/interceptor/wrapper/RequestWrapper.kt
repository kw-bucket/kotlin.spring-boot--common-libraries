package com.kw.starter.common.interceptor.wrapper

import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.util.Collections
import java.util.Enumeration
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class RequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    private val additionalHeaders: MutableMap<String, String> = mutableMapOf()
    val body: String = request.inputStream.bufferedReader().use { it.readText() }

    fun addHeader(name: String, value: String) {
        additionalHeaders[name] = value
    }

    override fun getHeader(name: String?): String? {
        val headerName: String? = name?.lowercase()
        val headerValue: String? = additionalHeaders[headerName]

        headerValue ?: return super.getHeader(headerName)

        return headerValue
    }

    override fun getHeaderNames(): Enumeration<String> =
        Collections.enumeration(super.getHeaderNames().toList() + additionalHeaders.keys)

    override fun getHeaders(name: String?): Enumeration<String> {
        val headerName: String? = name?.lowercase()
        val headerValues = (super.getHeaders(headerName).toList() + additionalHeaders[headerName]).filterNotNull()

        return Collections.enumeration(headerValues)
    }

    override fun getInputStream(): ServletInputStream {
        val byteArrayInputStream = ByteArrayInputStream(body.toByteArray())

        return object : ServletInputStream() {
            override fun isFinished(): Boolean = false

            override fun isReady(): Boolean = false

            override fun setReadListener(readListener: ReadListener) {}

            override fun read(): Int = byteArrayInputStream.read()
        }
    }

    override fun getReader(): BufferedReader = BufferedReader(InputStreamReader(this.inputStream))
}
