package com.theappengers.schemas

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object RoomsToUsersTable : IntIdTable("RoomsToUsers") {
    val roomId = reference("room_id", RoomsTable, ReferenceOption.CASCADE)
    val userId = reference("user_id", UsersTable, ReferenceOption.CASCADE)
}

fun RoomsToUsersTable.addUserToRoom(roomId: UUID, userId: UUID) {
    transaction {
        RoomsToUsersTable.insert {
            it[RoomsToUsersTable.roomId] = roomId
            it[RoomsToUsersTable.userId] = userId
        }
    }
}