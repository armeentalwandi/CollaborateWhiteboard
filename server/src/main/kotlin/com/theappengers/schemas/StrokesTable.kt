package com.theappengers.schemas

import SerializableStroke
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object StrokesTable : Table() {
    val strokeId = uuid("strokeId").uniqueIndex() // UUID as primary key
    val userId = uuid("userId") // foreign key reference to UsersTable
    val roomId = uuid("roomId")
    val serializedStroke = text("serializedStroke")
}

data class UpdateStrokeRequest(
    val strokeId: UUID,
    val serializedStroke: String
)

fun StrokesTable.createStroke(stroke: SerializableStroke, serializedStroke: String) {
    val userid = stroke.userId
    val strokeid = stroke.strokeId
    val roomId = stroke.roomId

    transaction {
        StrokesTable.insert {
            it[StrokesTable.strokeId] = UUID.fromString(strokeid)
            it[StrokesTable.roomId] = UUID.fromString(roomId)
            it[StrokesTable.userId] = UUID.fromString(userid)
            it[StrokesTable.serializedStroke] = serializedStroke
        }
    }
}

fun StrokesTable.getRoomStrokes(roomId: String?): List<String> {
    var allStrokes: List<String> = listOf()
    transaction {
        allStrokes = StrokesTable.select {
            StrokesTable.roomId eq UUID.fromString(roomId)
        }.map { it[serializedStroke] }
    }
    return allStrokes
}

fun StrokesTable.deleteStroke(strokeId: String) {
    val strokeIdUUID = UUID.fromString(strokeId)
    transaction {
        StrokesTable.deleteWhere { StrokesTable.strokeId eq strokeIdUUID }
    }
}

fun StrokesTable.updateStrokeRow(updateRequest: UpdateStrokeRequest) {
    transaction {
        val row = StrokesTable.select { strokeId eq updateRequest.strokeId }
            .singleOrNull()

        if (row != null) {
            StrokesTable.update({ strokeId eq updateRequest.strokeId }) {
                it[serializedStroke] = updateRequest.serializedStroke
            }
        }
    }
}


