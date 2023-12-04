package com.theappengers.controllers

import RoomData
import com.theappengers.services.RoomsService
import com.theappengers.services.RoomsToUsersService
import io.ktor.http.*
import java.util.*

class RoomController {

    private val roomsToUsersService = RoomsToUsersService()
    private val roomsService = RoomsService()

    fun getUserRooms(userId: String?) : Pair<HttpStatusCode, Any> {
        return if (userId != null) {
            val userIdUUID = UUID.fromString(userId)
            val rooms = roomsToUsersService.fetchUserRooms(userIdUUID)
            Pair(HttpStatusCode.OK, rooms)
        } else {
            Pair(HttpStatusCode.BadRequest, "Invalid or missing userId parameter")
        }
    }

    fun getRoomByCode(roomCode: String?): Pair<HttpStatusCode, Any> {
        return if (roomCode != null) {
            val room = roomsService.findRoomByCode(roomCode)
            if (room != null) {
                Pair(HttpStatusCode.OK, room)
            } else {
                Pair(HttpStatusCode.NotFound, "Room with code $roomCode not found")
            }
        } else {
            Pair(HttpStatusCode.BadRequest, "Invalid or missing roomCode")
        }
    }

    fun removeUserFromRoom(roomId: String?, userId: String?): Pair<HttpStatusCode, String> {
        return if (roomId == null || userId == null) {
            Pair(HttpStatusCode.BadRequest, "Missing roomId or userId")
        } else {
            try {
                val roomIdUUID = UUID.fromString(roomId)
                val userIdUUID = UUID.fromString(userId)
                roomsToUsersService.removeUserFromRoom(roomIdUUID, userIdUUID)
                Pair(HttpStatusCode.OK, "User removed from room successfully")
            } catch (e: IllegalArgumentException) {
                Pair(HttpStatusCode.BadRequest, "Invalid roomId or userId")
            } catch (e: Exception) {
                Pair(HttpStatusCode.InternalServerError, e.message ?: "Internal server error")
            }
        }
    }

    fun createRoom(roomData: RoomData): Pair<HttpStatusCode, Any> {
        return try {
            val createdRoom = roomsService.createRoom(
                roomData.roomName,
                roomData.roomCode,
                UUID.fromString(roomData.createdBy),
                roomData.isCourse
            )
            if (createdRoom != null) {
                roomsToUsersService.addUserToRoom(
                    UUID.fromString(createdRoom.roomId),
                    UUID.fromString(createdRoom.createdBy)
                )
                Pair(HttpStatusCode.Created, createdRoom)
            } else {
                Pair(HttpStatusCode.InternalServerError, "Failed to create room")
            }
        } catch (e: Exception) {
            Pair(HttpStatusCode.BadRequest, "Invalid room data")
        }
    }

    fun addUserToRoom(roomId: String?, userId: String?): Pair<HttpStatusCode, String> {
        return if (roomId == null || userId == null) {
            Pair(HttpStatusCode.BadRequest, "Missing roomId or userId")
        } else {
            try {
                roomsToUsersService.addUserToRoom(UUID.fromString(roomId), UUID.fromString(userId))
                Pair(HttpStatusCode.OK, "User added to room successfully")
            } catch (e: IllegalArgumentException) {
                Pair(HttpStatusCode.BadRequest, "Invalid roomId or userId")
            } catch (e: Exception) {
                Pair(HttpStatusCode.InternalServerError, e.message ?: "Internal server error")
            }
        }
    }

    fun deleteRoom(roomCode: String?): Pair<HttpStatusCode, String> {
        return if (roomCode != null) {
            val room = roomsService.findRoomByCode(roomCode)
            if (room != null) {
                val deleted = roomsService.deleteRoom(UUID.fromString(room.roomId))
                if (deleted) {
                    Pair(HttpStatusCode.OK, "Room deleted successfully")
                } else {
                    Pair(HttpStatusCode.InternalServerError, "Error deleting room")
                }
            } else {
                Pair(HttpStatusCode.NotFound, "Room not found")
            }
        } else {
            Pair(HttpStatusCode.BadRequest, "Missing or incorrect room code")
        }
    }
}