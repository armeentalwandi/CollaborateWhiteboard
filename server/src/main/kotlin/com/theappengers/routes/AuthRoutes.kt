package com.theappengers.routes

import LoginRequest
import LoginResponse
import RegisterRequest
import RegisterResponse
import User
import com.theappengers.configs.JwtConfig
import com.theappengers.schemas.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Routing.authRoutes() {
    route("/auth") {
        post("/login") {
            val text = call.receiveText()
            val loginRequest = Json.decodeFromString<LoginRequest>(text)

            if (!UsersTable.doesEmailExist(loginRequest.email)) {
                call.respond(HttpStatusCode.BadRequest, LoginResponse("Invalid Credentials", null))
                return@post
            }

            val user = UsersTable.findUserByEmail(loginRequest.email)

            if (user != null && UsersTable.isValidPassword(loginRequest.password, user.hashedPassword)) {
                val token = JwtConfig.makeToken(user)
                call.respond(LoginResponse(token, User(user.userId.toString(), user.email, user.firstName, user.lastName, user.authLevel)))
            } else {
                call.respond(HttpStatusCode.Unauthorized, LoginResponse("Invalid Credentials", null))
            }
        }

        post("/register") {
            val text = call.receiveText()
            val registerRequest = Json.decodeFromString<RegisterRequest>(text)

            if (UsersTable.doesEmailExist(registerRequest.email)) {
                call.respond(HttpStatusCode.BadRequest, RegisterResponse("User Exists Already"))
            }

            val hashedPassword = UsersTable.hashPassword(registerRequest.password)
            val newUser = UsersTable.createUser(registerRequest.email, hashedPassword, registerRequest.firstName, registerRequest.lastName, registerRequest.authLevel)

            val token = JwtConfig.makeToken(newUser!!)
            call.respond(HttpStatusCode.OK, RegisterResponse(token))
        }
    }
}