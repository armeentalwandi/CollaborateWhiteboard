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

    install(Authentication) {
        jwt {
            realm = "theappengers.io"
            verifier(JwtConfig.verifier)
            validate {
                val userId = it.payload.getClaim("id")
                val user = UsersTable.findUserById(userId = UUID.fromString(userId.toString()))
                if (user != null) JWTPrincipal(it.payload) else null
            }
        }
    }
}


