package com.seanshubin.factorio.deploy.capture

import com.seanshubin.factorio.deploy.GlobalConstants
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class OutputStreamAsLineEvents(private val lineCaptured: (String) -> Unit) : OutputStream() {
    private val byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
    override fun write(b: Int) {
        when (b) {
            '\n'.toInt() -> {
                emitLine()
                byteArrayOutputStream.reset()
            }
            else -> {
                byteArrayOutputStream.write(b)
            }
        }
    }

    override fun flush() {
        emitRemaining()
    }

    override fun close() {
        emitRemaining()
    }

    private fun emitRemaining() {
        if (byteArrayOutputStream.size() > 0) {
            emitLine()
            byteArrayOutputStream.reset()
        }
    }

    private fun emitLine() {
        val text = String(byteArrayOutputStream.toByteArray(), GlobalConstants.charset)
        lineCaptured(text)
    }
}
