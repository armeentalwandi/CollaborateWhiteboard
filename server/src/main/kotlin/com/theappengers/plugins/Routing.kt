package com.theappengers.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import com.theappengers.routes.*

fun Application.configureRouting() {
    routing {
        strokeRoutes()
    }
}
