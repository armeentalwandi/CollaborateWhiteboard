package com.theappengers.services

import Room
import com.theappengers.schemas.*
import java.util.UUID

class RoomsService {

    fun createRoom(roomName: String, roomCode: String, createdBy: UUID, isCourse: Boolean): Room? {
        return RoomsTable.createRoom(roomName, roomCode, createdBy, isCourse)
    }

    fun findRoomByCode(roomCode: String): Room? {
        return RoomsTable.findRoomByCode(roomCode)
    }

    fun findRoomById(roomId: UUID): Room? {
        return RoomsTable.findRoomById(roomId)
    }

    fun updateRoom(roomId: UUID, newName: String, newCode: String): Boolean {
        return RoomsTable.updateRoom(roomId, newName, newCode)
    }

    fun deleteRoom(roomId: UUID): Boolean {
        return RoomsTable.deleteRoom(roomId)
    }
}
