package com.theappengers.routes

import RoomData
import com.theappengers.controllers.RoomController
import com.theappengers.schemas.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*


fun Routing.roomRoutes() {

    val roomController = RoomController()

    route("/rooms") {
        get("/user/{userId}") {
            val response = roomController.getUserRooms(call.parameters["userId"])
            call.respond(response.first, response.second)
        }

        get("/room/{roomCode}") {
            val response = roomController.getRoomByCode(call.parameters["roomCode"])
            call.respond(response.first, response.second)
        }

        delete("/room/{roomId}/user/{userId}") {
            val response = roomController.removeUserFromRoom(
                call.parameters["roomId"],
                call.parameters["userId"]
            )
            call.respond(response.first, response.second)
        }

        post("/create") {
            val roomData = call.receive<RoomData>() // Assuming RoomData is your data class
            val response = roomController.createRoom(roomData)
            call.respond(response.first, response.second)
        }

        post("/room/{roomId}/add/{userId}") {
            val response = roomController.addUserToRoom(
                call.parameters["roomId"],
                call.parameters["userId"]
            )
            call.respond(response.first, response.second)
        }

        delete( "/delete/{roomCode}") {
            val response = roomController.deleteRoom(call.parameters["roomCode"])
            call.respond(response.first, response.second)
        }
    }
}