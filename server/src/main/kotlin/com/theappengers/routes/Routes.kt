package com.theappengers.routes

import SerializableStroke
import UpdateStrokesRequest
import com.theappengers.schemas.StrokesTable
import com.theappengers.schemas.StrokesTable.serializedStroke
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

                transaction {
                    StrokesTable.insert {
                        it[strokeId] = UUID.fromString(strokeid)
                        it[userId] = UUID.fromString(userid)
                        it[serializedStroke] = text
                    }
                }

                call.respond(HttpStatusCode.OK, "Stroke added successfully")
            }

            get("/all") {
                var allStrokes: List<String> = listOf();
                transaction {
                    allStrokes = StrokesTable.selectAll().toList().map {
                        it[serializedStroke]
                    }
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