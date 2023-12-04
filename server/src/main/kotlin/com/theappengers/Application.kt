package com.theappengers

import com.theappengers.configs.JwtConfig
import com.theappengers.plugins.*
import com.theappengers.providers.DatabaseProvider
import com.theappengers.schemas.UsersTable
import com.theappengers.schemas.findUserById
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.util.*


val ENVIRONMENT = "local"

fun main() {

    var host = "127.0.0.1"
    if (ENVIRONMENT == "remote" || ENVIRONMENT == "docker-local") {
        host = "0.0.0.0"
    } else if (ENVIRONMENT == "local") {
        host = "127.0.0.1"
    }

    embeddedServer(Netty, port = 8080, host = host, module = Application::module)
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


