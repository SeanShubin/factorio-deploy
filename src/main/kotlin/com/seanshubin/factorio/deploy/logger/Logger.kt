package com.seanshubin.factorio.deploy.logger

import com.seanshubin.factorio.deploy.http.Request
import com.seanshubin.factorio.deploy.http.Response

interface Logger{
    fun requestEvent(request: Request)
    fun responseEvent(request: Request, response: Response)
}