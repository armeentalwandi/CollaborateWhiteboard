package com.theappengers

import com.theappengers.plugins.*
import com.theappengers.providers.DatabaseProvider
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSockets()
    configureSerialization()
    configureSecurity()
    configureRouting()
    val databaseProvider = DatabaseProvider()
    databaseProvider.init()
}