package com.theappengers.routes

import LoginRequest
import RegisterRequest
import com.theappengers.controllers.AuthController
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.serialization.json.Json

fun Routing.authRoutes() {
    val authController = AuthController()

    route("/auth") {
        post("/login") {
            val loginRequest = Json.decodeFromString<LoginRequest>(call.receiveText())
            val response = authController.login(loginRequest)
            call.respond(response.first, response.second)
        }

        post("/register") {
            val registerRequest = Json.decodeFromString<RegisterRequest>(call.receiveText())
            val response = authController.register(registerRequest)
            call.respond(response.first, response.second)
        }
    }
}
