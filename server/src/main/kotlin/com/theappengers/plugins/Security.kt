package com.theappengers.plugins

import com.theappengers.configs.JwtConfig
import com.theappengers.schemas.UsersTable
import com.theappengers.schemas.findUserById
import com.theappengers.services.UsersService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("jwt") {
            verifier(JwtConfig.verifier)
            validate { credential ->
                val userId = credential.payload.getClaim("id")
                if (userId.isNull || userId.asString() == null) {
                    null
                } else {
                    val user = UsersService().findUserById(UUID.fromString(userId.asString()))
                    user?.let { JWTPrincipal(credential.payload) }
                }
            }
        }
    }
}
