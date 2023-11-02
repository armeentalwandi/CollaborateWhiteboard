package com.theappengers.routes

import com.theappengers.LoginRequest
import com.theappengers.RegisterRequest
import com.theappengers.configs.JwtConfig
import com.theappengers.schemas.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.authRoutes() {
    route("/auth") {
        post("/login") {
            val loginRequest = call.receive<LoginRequest>()

            if (!UsersTable.doesEmailExist(loginRequest.email)) {
                call.respond(HttpStatusCode.BadRequest, "Email does not exist")
                return@post
            }

            val user = UsersTable.findUserByEmail(loginRequest.email)

            if (user != null && UsersTable.isValidPassword(loginRequest.password, user.hashedPassword)) {
                val token = JwtConfig.makeToken(user)
                call.respond(mapOf("auth-token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid Credentials")
            }
        }

        post("/register") {
            val registerRequest = call.receive<RegisterRequest>()

            println(registerRequest)

            if (UsersTable.doesEmailExist(registerRequest.email)) {
                call.respond(HttpStatusCode.BadRequest, "Email already exists")
            }

            val hashedPassword = UsersTable.hashPassword(registerRequest.password)
            val newUser = UsersTable.createUser(registerRequest.email, hashedPassword, registerRequest.firstName, registerRequest.lastName, registerRequest.authLevel)

            val token = JwtConfig.makeToken(newUser!!)
            call.respond(HttpStatusCode.OK, mapOf("auth-token" to token))
        }
    }
}