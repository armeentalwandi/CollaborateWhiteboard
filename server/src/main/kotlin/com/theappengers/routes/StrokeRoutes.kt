package com.theappengers.routes

import SerializableStroke
import UpdateStrokesRequest
import com.theappengers.controllers.StrokeController
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.serialization.json.Json

fun Routing.strokeRoutes() {
    val strokeController = StrokeController()

    route("/strokes") {
        post {
            val text = call.receiveText()
            val deserialized = Json.decodeFromString<SerializableStroke>(text)
            val response = strokeController.createStroke(deserialized, text)
            call.respond(response.first, response.second)
        }

        get("/all/{roomId}") {
            val response = strokeController.getRoomStrokes(call.parameters["roomId"])
            call.respond(response.first, response.second)
        }

        delete("/{strokeId}") {
            val response = strokeController.deleteStroke(call.parameters["strokeId"])
            call.respond(response.first, response.second)
        }

        put("/update") {
            val updateStrokesRequest = Json.decodeFromString<UpdateStrokesRequest>(call.receiveText())
            val response = strokeController.updateStrokes(updateStrokesRequest)
            call.respond(response.first, response.second)
        }
    }
}
