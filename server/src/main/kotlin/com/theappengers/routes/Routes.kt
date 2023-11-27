package com.theappengers.routes

import CreateRoomData
import SerializableStroke
import UpdateStrokesRequest
import com.theappengers.schemas.*
import com.theappengers.schemas.StrokesTable
import com.theappengers.schemas.StrokesTable.serializedStroke
import com.theappengers.schemas.StrokesTable.roomId
import com.theappengers.schemas.UpdateStrokeRequest
import com.theappengers.schemas.updateStrokeRow
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Routing.strokeRoutes() {
    // authenticate("jwt") {
        route("/strokes") {
            post {
                val text = call.receiveText()
                val deserialized = Json.decodeFromString<SerializableStroke>(text)
                val userid = deserialized.userId
                val strokeid = deserialized.strokeId
                val roomId = deserialized.roomId

                transaction {
                    StrokesTable.insert {
                        it[StrokesTable.strokeId] = UUID.fromString(strokeid)
                        it[StrokesTable.roomId] = UUID.fromString(roomId)
                        it[StrokesTable.userId] = UUID.fromString(userid)
                        it[StrokesTable.serializedStroke] = text
                    }
                }

                call.respond(HttpStatusCode.OK, "Stroke added successfully")
            }

            get("/all/{roomId}") {
                val roomId = call.parameters["roomId"]?: null
                var allStrokes: List<String> = listOf()
                transaction {
                    allStrokes = StrokesTable.select {
                        StrokesTable.roomId eq UUID.fromString(roomId)
                    }.map { it[serializedStroke] }
                }
                call.respond(HttpStatusCode.OK, allStrokes)
            }

            delete("/{strokeId}") {
                val strokeId = call.parameters["strokeId"]?: null
                if (strokeId != null) {
                    val strokeIdUUID = UUID.fromString(strokeId)
                    transaction {
                        // Logic to delete the stroke with the given ID from the database.
                        StrokesTable.deleteWhere { StrokesTable.strokeId eq strokeIdUUID }
                    }
                    call.respond(HttpStatusCode.OK, "Stroke deleted successfully")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing strokeId parameter")
                }
            }

            put ("/update"){
                val text = call.receiveText()
                val deserialized = Json.decodeFromString<UpdateStrokesRequest>(text)

                println(deserialized.serializedStrokes)

                transaction {
                    deserialized.serializedStrokes.forEach { stroke ->
                        val strokeString = Json.encodeToString(stroke)
                        StrokesTable.updateStrokeRow(UpdateStrokeRequest(UUID.fromString(stroke.strokeId), strokeString))
                    }
                }
            }
        }
    //}
}

fun Routing.roomRoutes() {
//    authenticate("jwt") {
        route("/rooms") {
            get("/{userId}") {
                val userId = call.parameters["userId"]?: null
                println(userId)
                if (userId != null) {
                    val userIdUUID = UUID.fromString(userId)
                    val rooms = RoomsToUsersTable.fetchUserRooms(userIdUUID)
                    call.respond(HttpStatusCode.OK, rooms)
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing userId parameter")
                }
            }

            post("/create") {
                // Extract room details from the request
                val room = call.receive<CreateRoomData>() // Assuming Room is your data class
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
        }
//    }
}