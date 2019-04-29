package com.seanshubin.factorio.deploy.capture

import java.io.OutputStream

class OutputStreamStoredAsLines(private val lineEventNotification: (String) -> Unit) : OutputStream() {
    private val outputStreamAsLineEvents = OutputStreamAsLineEvents(::lineEvent)
    private val mutableLines = mutableListOf<String>()
    val lines = mutableLines
    override fun write(b: Int) {
        outputStreamAsLineEvents.write(b)
    }

    private fun lineEvent(line: String) {
        mutableLines.add(line)
        lineEventNotification(line)
    }

    override fun flush() {
        outputStreamAsLineEvents.flush()
    }

    override fun close() {
        outputStreamAsLineEvents.close()
    }
}
