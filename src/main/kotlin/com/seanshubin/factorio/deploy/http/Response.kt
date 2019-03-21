package com.seanshubin.factorio.deploy.http

import com.seanshubin.factorio.deploy.collection.CollectionUtil.exactlyOne
import com.seanshubin.factorio.deploy.string.StringUtil.toUtf8
import java.net.HttpURLConnection
import java.time.Duration

data class Response(
    val statusCode: Int,
    val body: List<Byte>,
    val headers: List<Pair<String, String>>,
    val duration: Duration
) {
    val isRedirect: Boolean
        get() = when (statusCode) {
            HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP -> true
            else -> false
        }

    val followRedirect: Request
        get() {
            val uri = headers.lookup("location")
            val method = "get"
            val body = emptyList<Byte>()
            return Request(uri, method, body, emptyList())
        }

    private fun List<Pair<String, String>>.lookup(name: String): String =
        this.filter { (key, _) -> name.equals(key, ignoreCase = true) }.exactlyOne().second

    fun toTable(): List<List<Any>> =
        listOf(
            listOf("statusCode", statusCode),
            listOf("body", body.toUtf8()),
            listOf("duration", duration)
        ) + headersToTable()

    private fun headersToTable(): List<List<Any>> =
        headers.map { (name, value) -> listOf(name, value) }
}
