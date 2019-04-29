package com.seanshubin.factorio.deploy.capture

import java.io.OutputStream

class NormalizeNewlineAdapter(private val delegate: OutputStream) : OutputStream() {
    private var lastCharWasCarriageReturn = false
    override fun write(b: Int) {
        when (b) {
            '\n'.toInt() -> {
                lastCharWasCarriageReturn = false
                delegate.write(b)
            }
            '\r'.toInt() -> {
                if (lastCharWasCarriageReturn) {
                    delegate.write('\n'.toInt())
                } else {
                    lastCharWasCarriageReturn = true
                }
            }
            else -> {
                if (lastCharWasCarriageReturn) {
                    lastCharWasCarriageReturn = false
                    delegate.write('\n'.toInt())
                }
                delegate.write(b)
            }

        }
    }
}
