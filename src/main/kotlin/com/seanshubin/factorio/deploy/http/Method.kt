package com.seanshubin.factorio.deploy.http

import java.net.http.HttpRequest
import com.seanshubin.factorio.deploy.collection.CollectionUtil.exactlyOne

enum class Method {
    GET {
        override fun applyToBuilder(request: Request, builder: HttpRequest.Builder) {
            builder.GET()
        }
    },
    POST {
        override fun applyToBuilder(request: Request, builder: HttpRequest.Builder) {
            builder.POST(HttpRequest.BodyPublishers.ofByteArray(request.body.toByteArray()))
        }
    },
    PUT {
        override fun applyToBuilder(request: Request, builder: HttpRequest.Builder) {
            builder.PUT(HttpRequest.BodyPublishers.ofByteArray(request.body.toByteArray()))
        }
    },
    DELETE {
        override fun applyToBuilder(request: Request, builder: HttpRequest.Builder) {
            builder.DELETE()
        }
    };

    abstract fun applyToBuilder(request: Request, builder: HttpRequest.Builder);

    companion object {
        fun fromString(s: String): Method =
            Method.values().filter { it.name.equals(s, ignoreCase = true) }.exactlyOne()
    }
}
