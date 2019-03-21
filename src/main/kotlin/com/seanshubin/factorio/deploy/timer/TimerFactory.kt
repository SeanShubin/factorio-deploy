package com.seanshubin.factorio.deploy.timer

import java.time.Clock

object TimerFactory {
    fun createDefault(): Timer {
        val clock = Clock.systemUTC()
        return Timer(clock)
    }
}
