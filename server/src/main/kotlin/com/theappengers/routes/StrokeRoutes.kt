package com.theappengers.routes

import RoomData
import SerializableStroke
import UpdateStrokesRequest
import com.theappengers.schemas.*
import com.theappengers.schemas.StrokesTable
import com.theappengers.schemas.StrokesTable.serializedStroke
import com.theappengers.schemas.UpdateStrokeRequest
import com.theappengers.schemas.updateStrokeRow
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Routing.strokeRoutes() {
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
            transaction {
                deserialized.serializedStrokes.forEach { stroke ->
                    val strokeString = Json.encodeToString(stroke)
                    StrokesTable.updateStrokeRow(UpdateStrokeRequest(UUID.fromString(stroke.strokeId), strokeString))
                }
            }
        }
    }
}