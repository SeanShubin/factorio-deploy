package com.seanshubin.factorio.deploy.collection

object CollectionUtil {
    fun <T> List<T>.exactlyOne(): T = when {
        this.isEmpty() -> throw RuntimeException("Expected exactly one element, was empty")
        this.size > 1 -> throw RuntimeException("Expected exactly one element, got ${this.size}")
        else -> this[0]
    }
}
