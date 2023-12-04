package com.theappengers.services

import Room
import com.theappengers.schemas.*
import java.util.UUID

class RoomsToUsersService {

    fun addUserToRoom(roomId: UUID, userId: UUID) {
        RoomsToUsersTable.addUserToRoom(roomId, userId)
    }

    fun removeUserFromRoom(roomId: UUID, userId: UUID) {
        RoomsToUsersTable.removeUserFromRoom(roomId, userId)
    }

    fun fetchUserRooms(userId: UUID): List<Room> {
        return RoomsToUsersTable.fetchUserRooms(userId)
    }

    fun fetchRoomUsers(roomId: UUID): List<User> {
        return RoomsToUsersTable.fetchRoomUsers(roomId)
    }
}
