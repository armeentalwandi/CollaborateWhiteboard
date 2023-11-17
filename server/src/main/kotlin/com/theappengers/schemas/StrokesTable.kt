package com.theappengers.schemas

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
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

fun StrokesTable.updateStrokeRow(updateRequest: UpdateStrokeRequest) {
    transaction {
        // Find the row with the specified strokeId
        val row = StrokesTable.select { StrokesTable.strokeId eq updateRequest.strokeId }
            .singleOrNull()

        if (row != null) {
            // Update the serializedStroke column
            StrokesTable.update({ StrokesTable.strokeId eq updateRequest.strokeId }) {
                it[serializedStroke] = updateRequest.serializedStroke
            }
        } else {
            // Handle the case where the row with the given strokeId doesn't exist
            // You may want to throw an exception or handle it differently
        }
    }
}


