package com.seanshubin.factorio.deploy.capture

import java.io.InputStream

object NoInput : InputStream() {
    override fun read(): Int = -1
}
