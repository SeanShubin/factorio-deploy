package com.seanshubin.factorio.deploy.http

interface Http {
    fun send(request: Request): Response
    fun sendAndFollowRedirects(request: Request): List<RequestResponse>
}
