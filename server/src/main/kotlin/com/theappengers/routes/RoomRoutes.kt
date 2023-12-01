package com.theappengers.routes

import RoomData
import com.theappengers.schemas.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*


fun Routing.roomRoutes() {
    route("/rooms") {
        get("/user/{userId}") {
            val userId = call.parameters["userId"]?: null
            if (userId != null) {
                val userIdUUID = UUID.fromString(userId)
                val rooms = RoomsToUsersTable.fetchUserRooms(userIdUUID)
                call.respond(HttpStatusCode.OK, rooms)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId parameter")
            }
        }

        get("/room/{roomCode}") {
            val roomCode = call.parameters["roomCode"]?: null
            if (roomCode != null) {
                val room = RoomsTable.findRoomByCode(roomCode)
                if (room != null) {
                    call.respond(HttpStatusCode.OK, room)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Room with code $roomCode not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing roomCode")
            }
        }

        delete("/room/{roomId}/user/{userId}") {
            val roomId = call.parameters["roomId"]
            val userId = call.parameters["userId"]

            if (roomId == null || userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing roomId or userId")
                return@delete
            }

            try {
                val roomIdUUID = UUID.fromString(roomId)
                val userIdUUID = UUID.fromString(userId)
                RoomsToUsersTable.removeUserFromRoom(roomIdUUID, userIdUUID)
                call.respond(HttpStatusCode.OK, "User removed from room successfully")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid roomId or userId")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.message ?: "Internal server error")
            }
        }

        post("/create") {
            // Extract room details from the request
            val room = call.receive<RoomData>() // Assuming Room is your data class
            // Add logic to create the room in the database
            try {
                val createdRoom = RoomsTable.createRoom(room.roomName, room.roomCode, UUID.fromString(room.createdBy))
                if (createdRoom != null) {
                    RoomsToUsersTable.addUserToRoom(UUID.fromString(createdRoom.roomId), UUID.fromString(createdRoom.createdBy))
                    call.respond(HttpStatusCode.Created, createdRoom)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create room")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid room data")
            }
        }

        post("/room/{roomId}/add/{userId}") {
            val roomId = UUID.fromString(call.parameters["roomId"])
            val userId = UUID.fromString(call.parameters["userId"])

            if (roomId == null || userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing roomId or userId")
                return@post
            }

            try {
                RoomsToUsersTable.addUserToRoom(roomId, userId)
                call.respond(HttpStatusCode.OK, "User added to room successfully")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Invalid roomId or userId")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.message ?: "Internal server error")
            }
        }

        delete( "/delete/{roomCode}") {
            val roomCode = call.parameters["roomCode"] ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing or incorrect room code")

            val room = RoomsTable.findRoomByCode(roomCode)
            if (room != null) {
                val deleted = RoomsTable.deleteRoom(UUID.fromString(room.roomId))
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Room deleted successfully")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Error deleting room")
                }
            } else {
                call.respond(HttpStatusCode.NotFound, "Room not found")
            }
        }
    }
}