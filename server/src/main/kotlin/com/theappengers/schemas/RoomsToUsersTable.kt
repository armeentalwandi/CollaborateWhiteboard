package com.theappengers.schemas

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

fun RoomsToUsersTable.removeUserFromRoom(roomId: UUID, userId: UUID) {
    transaction {
        RoomsToUsersTable.deleteWhere {
            (RoomsToUsersTable.userId eq userId) and (RoomsToUsersTable.roomId eq roomId)
        }
    }
}

fun RoomsToUsersTable.fetchUserRooms(userId: UUID): List<Room> {
    val rooms = mutableListOf<Room>()
    transaction {
        (RoomsToUsersTable innerJoin  RoomsTable).select {
            RoomsToUsersTable.userId eq userId
        }.forEach { resultRow ->
            rooms.add(
                Room(
                    roomId = resultRow[RoomsTable.id].value,
                    roomName = resultRow[RoomsTable.roomName],
                    roomCode = resultRow[RoomsTable.roomCode],
                    createdBy = resultRow[RoomsTable.createdBy].value,
                )
            )
        }
    }
    return rooms
}

fun RoomsToUsersTable.fetchRoomUsers(roomId: UUID): List<User> {
    val users = mutableListOf<User>()
    transaction {
        (RoomsToUsersTable innerJoin  UsersTable).select {
            RoomsToUsersTable.roomId eq roomId
        }.forEach { resultRow ->
            users.add(
                User(
                    userId = resultRow[UsersTable.id].value,
                    firstName = resultRow[UsersTable.firstName],
                    lastName = resultRow[UsersTable.lastName],
                    authLevel = resultRow[UsersTable.authLevel],
                    email = resultRow[UsersTable.email],
                    hashedPassword = resultRow[UsersTable.hashedPassword]
                )
            )
        }
    }
    return users
}