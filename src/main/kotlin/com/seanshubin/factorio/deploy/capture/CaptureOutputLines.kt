package com.seanshubin.factorio.deploy.capture

import com.seanshubin.factorio.deploy.GlobalConstants
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class CaptureOutputLines(val lineCaptured: (String) -> Unit) : OutputStream() {
    private val byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()
    private var lastCharWasCarriageReturn = false
    override fun write(b: Int) {
        when (b) {
            '\n'.toInt() -> {
                emitLine()
                lastCharWasCarriageReturn = false
                byteArrayOutputStream.reset()
            }
            '\r'.toInt() -> {
                if (lastCharWasCarriageReturn) {
                    emitLine()
                } else {
                    lastCharWasCarriageReturn = true
                }
            }
            else -> {
                if (lastCharWasCarriageReturn) {
                    emitLine()
                    lastCharWasCarriageReturn = false
                    byteArrayOutputStream.reset()
                    byteArrayOutputStream.write(b)
                } else {
                    byteArrayOutputStream.write(b)
                }
            }

        }
    }

    private fun emitLine() {
        val text = String(byteArrayOutputStream.toByteArray(), GlobalConstants.charset)
        lineCaptured(text)
    }
}
