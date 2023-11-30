package com.theappengers.schemas

import Room
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.Serial
import java.util.UUID
//import org.jetbrains.exposed.sql.javatime.datetime


object RoomsTable : UUIDTable("Rooms") {
    val roomName = varchar("room_name", 255)
    val roomCode = varchar("room_code", 10).uniqueIndex()  // Assuming codes are 10 characters long and unique
    val createdBy = reference("created_by", UsersTable)
}

fun RoomsTable.createRoom(roomName: String, roomCode: String, createdBy: UUID): Room? {
    var roomRow : ResultRow? = null
    transaction {
        val generatedKey = RoomsTable.insert {
            it[RoomsTable.roomName] = roomName
            it[RoomsTable.roomCode] = roomCode
            it[RoomsTable.createdBy] = createdBy
        } get RoomsTable.id

        roomRow = RoomsTable.select { RoomsTable.id eq generatedKey }.singleOrNull()
    }

    return if (roomRow != null) Room(
        roomId = roomRow!![id].value.toString(),
        roomName = roomRow!![RoomsTable.roomName],
        roomCode = roomRow!![RoomsTable.roomCode],
        createdBy = roomRow!![RoomsTable.createdBy].value.toString(),
    ) else null
}

fun RoomsTable.findRoomByCode(roomCode: String): Room? {
    var room: Room? = null
    transaction {
        val row = RoomsTable.select{ RoomsTable.roomCode eq roomCode }.singleOrNull()
        if (row != null) {
            room = Room(
                roomId = row[RoomsTable.id].value.toString(),
                roomName = row[roomName],
                roomCode = row[RoomsTable.roomCode],
                createdBy = row[createdBy].value.toString(),
//            createdAt = roomRow!![RoomsTable.createdAt]
            )
        }
    }
    return room
}

fun RoomsTable.findRoomById(roomId: UUID): Room? {
    var room: Room? = null
    transaction {
        val row = RoomsTable.select{ RoomsTable.id eq roomId }.single()
        room = Room(
            roomId = row[RoomsTable.id].value.toString(),
            roomName = row[roomName],
            roomCode = row[roomCode],
            createdBy = row[createdBy].value.toString(),
//            createdAt = roomRow!![RoomsTable.createdAt]
        )
    }
    return room
}

fun RoomsTable.updateRoom(roomId: UUID, newName: String, newCode: String): Boolean {
    var updatedRows = 0
    transaction {
        updatedRows = RoomsTable.update({ RoomsTable.id eq roomId }) {
            it[roomName] = newName
            it[roomCode] = newCode
        }

    }
    return updatedRows > 0 // Returns true if at least one row was updated
}

fun RoomsTable.deleteRoom(roomId: UUID): Boolean {
    var deletedRows = 0
    transaction { deletedRows = deleteWhere { RoomsTable.id eq roomId } }
    return deletedRows > 0 // Returns true if at least one row was deleted
}



