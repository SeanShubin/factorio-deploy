package com.seanshubin.factorio.deploy.timer

import java.time.Clock
import java.time.Duration

class Timer(private val clock: Clock) {
    fun <T> durationAndResult(f: () -> T): Pair<Duration, T> {
        val begin = clock.instant()
        val result = f()
        val end = clock.instant()
        val duration = Duration.between(begin, end)
        return Pair(duration, result)
    }
}
