package com.theappengers.routes

import SerializableStroke
import com.theappengers.StrokesTable
import com.theappengers.StrokesTable.serializedStroke
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.Identity.decode
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Routing.strokeRoutes() {
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


    }
}