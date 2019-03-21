package com.seanshubin.factorio.deploy.http

import com.seanshubin.factorio.deploy.timer.Timer
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HttpExec(
    private val client: HttpClient,
    private val timer: Timer,
    private val requestEvent: (Request) -> Unit,
    private val responseEvent: (Request, Response) -> Unit
) : Http {
    override fun send(request: Request): Response {
        requestEvent(request)
        val builder = HttpRequest.newBuilder()
        val uri = URI(request.uri)
        builder.uri(uri)
        request.headers.forEach { (name, value) -> builder.setHeader(name, value) }
        Method.fromString(request.method).applyToBuilder(request, builder)
        val httpRequest = builder.build()
        val bodyHandler = HttpResponse.BodyHandlers.ofByteArray()
        val (duration, httpResponse) = timer.durationAndResult { client.send(httpRequest, bodyHandler) }
        val responseHeaders =
            httpResponse.headers().map().map { (name, values) -> Pair(name, values.joinToString(",")) }
        val response = Response(httpResponse.statusCode(), httpResponse.body().toList(), responseHeaders, duration)
        responseEvent(request, response)
        return response
    }

    override fun sendAndFollowRedirects(request: Request): List<RequestResponse> {
        val result = mutableListOf<RequestResponse>()
        var currentRequest = request
        var currentResponse = send(request)
        result.add(RequestResponse(currentRequest, currentResponse))
        while (currentResponse.isRedirect) {
            currentRequest = currentResponse.followRedirect
            currentResponse = send(currentRequest)
            result.add(RequestResponse(currentRequest, currentResponse))
        }
        return result
    }
}
