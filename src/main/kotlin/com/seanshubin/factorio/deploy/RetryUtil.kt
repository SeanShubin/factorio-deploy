package com.seanshubin.factorio.deploy

import kotlinx.coroutines.*
import java.time.Duration

object RetryUtil {
    fun wait(attemptLimit:Int,timeBetweenAttempts:Duration, totalTimeLimit:Duration, isDone: () -> Boolean) {
        var attempt = 1
        var done = false
        val job = GlobalScope.launch {
            while (!done && attempt <= attemptLimit) {
                try {
                    if (isDone()) {
                        done = true
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                } finally {
                    attempt++
                    delay(timeBetweenAttempts.toMillis())
                }
            }
        }
        runBlocking {
            withTimeout(totalTimeLimit.toMillis()) {
                job.join()
            }
        }
    }
}
