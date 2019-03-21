package com.seanshubin.factorio.deploy

import com.seanshubin.factorio.deploy.http.Http
import com.seanshubin.factorio.deploy.http.Request

class FactorioApiImpl(private val http: Http,
                      private val url: String) : FactorioApi {
    override fun loadFactorio(): FactorioInfo {
        val request1 = Request(url, "GET", listOf(), listOf())
        val response1 = http.send(request1)
        assert(response1.statusCode == 302)
        val location = response1.headers.toMap().getValue("location")
        val fileName = FactorioUtil.factorioUriToFileName(location)
        return FactorioInfo(fileName)
    }
}
