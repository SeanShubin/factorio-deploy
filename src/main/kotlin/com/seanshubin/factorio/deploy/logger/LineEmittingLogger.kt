package com.seanshubin.factorio.deploy.logger

import com.seanshubin.factorio.deploy.http.Request
import com.seanshubin.factorio.deploy.http.Response
import com.seanshubin.factorio.deploy.table.RowStyleTableFormatter
import com.seanshubin.factorio.deploy.table.TableFormatter

class LineEmittingLogger(private val emitLine:(String)->Unit, private val tableFormatter: TableFormatter):Logger {
    override fun requestEvent(request: Request) {
        tableFormatter.format(request.toTable()).map(emitLine)
    }

    override fun responseEvent(request: Request, response: Response) {
        tableFormatter.format(response.toTable()).map(emitLine)
    }
}