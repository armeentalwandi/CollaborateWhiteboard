package com.theappengers.routes

import Stroke
import com.theappengers.StrokesTable
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.strokeRoutes() {
    route("/strokes") {
        post {
            val stroke = call.receive<Stroke>()
            println(stroke)
        }

    }
}