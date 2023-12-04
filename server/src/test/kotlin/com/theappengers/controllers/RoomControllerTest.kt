package com.theappengers.controllers

import Room
import RoomData
import com.theappengers.services.RoomsService
import com.theappengers.services.RoomsToUsersService
import io.ktor.http.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.reset
import java.lang.reflect.Field
import java.util.*

class RoomControllerTest {

    private lateinit var controller: RoomController
    private lateinit var mockRoomsService: RoomsService
    private lateinit var mockRoomsToUsersService: RoomsToUsersService
    private lateinit var rooms : List<Room>
    private lateinit var room : Room
    private lateinit var roomData: RoomData

    @BeforeEach
    fun setUp() {

        controller = RoomController()
        mockRoomsService = Mockito.mock(RoomsService::class.java)
        mockRoomsToUsersService = Mockito.mock(RoomsToUsersService::class.java)

        // Inject mock using reflection
        val field: Field = RoomController::class.java.getDeclaredField("roomsService")
        field.isAccessible = true
        field.set(controller, mockRoomsService)

        val field2: Field = RoomController::class.java.getDeclaredField("roomsToUsersService")
        field2.isAccessible = true
        field2.set(controller, mockRoomsToUsersService)

        rooms = emptyList()
        room = Room("062e7291-c93c-4fe8-86a8-26190e6c1318", "test", "validCode", "062e7291-c93c-4fe8-86a8-26190e6c1318")
        roomData = RoomData("test", "validCode", "062e7291-c93c-4fe8-86a8-26190e6c1318")
    }

    @AfterEach
    fun tearDown() {
        reset(mockRoomsService, mockRoomsToUsersService)
    }

    @Test
    fun getUserRooms_shouldReturnOKonSuccess() {
        // Arrange
        val userId = UUID.randomUUID().toString()
        `when`(mockRoomsToUsersService.fetchUserRooms(UUID.fromString(userId))).thenReturn(rooms)

        // Act
        val result = controller.getUserRooms(userId)

        // Assert
        assertEquals(HttpStatusCode.OK, result.first)
        assertEquals(rooms, result.second)
    }

    @Test
    fun getUserRooms_shouldReturnBadRequestOnNullUserId() {
        val result = controller.getUserRooms(null)
        // Assert
        assertEquals(HttpStatusCode.BadRequest, result.first)
        assertEquals("Invalid or missing userId parameter", result.second)
    }

    @Test
    fun getRoomByCode_shouldReturnOKonSuccess() {
        // Arrange
        val roomCode = "validCode"
        `when`(mockRoomsService.findRoomByCode(roomCode)).thenReturn(room)

        // Act
        val result = controller.getRoomByCode(roomCode)

        // Assert
        assertEquals(HttpStatusCode.OK, result.first)
        assertEquals(room, result.second)
    }

    @Test
    fun getRoomByCode_shouldReturnNotFoundIfRoomDoesNotExist() {
        // Arrange
        val roomCode = "invalidCode"
        `when`(mockRoomsService.findRoomByCode(roomCode)).thenReturn(null)

        // Act
        val result = controller.getRoomByCode(roomCode)

        // Assert
        assertEquals(HttpStatusCode.NotFound, result.first)
        assertEquals("Room with code $roomCode not found", result.second)
    }

    @Test
    fun getRoomByCode_shouldReturnBadRequestIfCodeIsNull() {
        // Act
        val result = controller.getRoomByCode(null)

        // Assert
        assertEquals(HttpStatusCode.BadRequest, result.first)
        assertEquals("Invalid or missing roomCode", result.second)
    }


    @Test
    fun removeUserFromRoom_shouldReturnOKonSuccess() {
        // Arrange
        val roomId = UUID.randomUUID().toString()
        val userId = UUID.randomUUID().toString()

        // Act
        val result = controller.removeUserFromRoom(roomId, userId)

        // Assert
        assertEquals(HttpStatusCode.OK, result.first)
        assertEquals("User removed from room successfully", result.second)
    }

    @Test
    fun removeUserFromRoom_shouldReturnBadRequestIfParametersAreMissing() {
        // Act
        val result = controller.removeUserFromRoom(null, null)

        // Assert
        assertEquals(HttpStatusCode.BadRequest, result.first)
        assertEquals("Missing roomId or userId", result.second)
    }

    @Test
    fun createRoom_shouldReturnCreatedOnSuccess() {
        // Arrange
        `when`(mockRoomsService.createRoom(roomData.roomName, roomData.roomCode, UUID.fromString(roomData.createdBy), false)).thenReturn(room)

        // Act
        val result = controller.createRoom(roomData)

        // Assert
        assertEquals(HttpStatusCode.Created, result.first)
        assertEquals(room, result.second)
    }

    @Test
    fun createRoom_shouldReturnBadRequestOnNullCreatedRoom() {
        `when`(mockRoomsService.createRoom(roomData.roomName, roomData.roomCode, UUID.fromString(roomData.createdBy), false)).thenReturn(null)

        // Act
        val result = controller.createRoom(roomData)

        // Assert
        assertEquals(HttpStatusCode.InternalServerError, result.first)
        assertEquals("Failed to create room", result.second)
    }

    @Test
    fun createRoom_shouldReturnBadRequestOnException() {
        // Arrange
        `when`(mockRoomsService.createRoom(roomData.roomName, roomData.roomCode, UUID.fromString(roomData.createdBy), false)).thenAnswer { throw Exception() }

        // Act
        val result = controller.createRoom(roomData)

        // Assert
        assertEquals(HttpStatusCode.BadRequest, result.first)
        assertEquals("Invalid room data", result.second)
    }

    @Test
    fun createRoom_shouldReturnBadRequestOnExceptionFromRoomsToUserService() {
        // Arrange
        `when`(mockRoomsService.createRoom(roomData.roomName, roomData.roomCode, UUID.fromString(roomData.createdBy), false)).thenReturn(room)
        `when`(mockRoomsToUsersService.addUserToRoom(UUID.fromString(room.roomId), UUID.fromString(room.createdBy))).thenAnswer { throw Exception() }

        // Act
        val result = controller.createRoom(roomData)

        // Assert
        assertEquals(HttpStatusCode.BadRequest, result.first)
        assertEquals("Invalid room data", result.second)
    }

    @Test
    fun addUserToRoom_shouldReturnOKonSuccess() {
        // Arrange
        val roomId = UUID.randomUUID().toString()
        val userId = UUID.randomUUID().toString()

        // Act
        val result = controller.addUserToRoom(roomId, userId)

        // Assert
        assertEquals(HttpStatusCode.OK, result.first)
        assertEquals("User added to room successfully", result.second)
    }

    @Test
    fun addUserToRoom_shouldReturnBadRequestIfParametersAreMissing() {
        // Act
        val result = controller.addUserToRoom(null, null)

        // Assert
        assertEquals(HttpStatusCode.BadRequest, result.first)
        assertEquals("Missing roomId or userId", result.second)
    }

    @Test
    fun addUserToRoom_shouldReturnBadRequestOnInvalidUUID() {
        // Act
        val result = controller.addUserToRoom("invalidRoomId", "invalidUserId")

        // Assert
        assertEquals(HttpStatusCode.BadRequest, result.first)
        assertEquals("Invalid roomId or userId", result.second)
    }

    @Test
    fun addUserToRoom_shouldReturnInternalServerErrorOnException() {
        // Arrange
        val roomId = UUID.randomUUID().toString()
        val userId = UUID.randomUUID().toString()
        `when`(mockRoomsToUsersService.addUserToRoom(UUID.fromString(roomId), UUID.fromString(userId)))
            .thenAnswer { throw Exception() }

        // Act
        val result = controller.addUserToRoom(roomId, userId)

        // Assert
        assertEquals(HttpStatusCode.InternalServerError, result.first)
        assertEquals("Internal server error", result.second)
    }

    @Test
    fun deleteRoom_shouldReturnOKonSuccess() {
        // Arrange
        val roomCode = "validCode"
        `when`(mockRoomsService.findRoomByCode(roomCode)).thenReturn(room)
        `when`(mockRoomsService.deleteRoom(UUID.fromString(room.roomId))).thenReturn(true)

        // Act
        val result = controller.deleteRoom(roomCode)

        // Assert
        assertEquals(HttpStatusCode.OK, result.first)
        assertEquals("Room deleted successfully", result.second)
    }

    @Test
    fun deleteRoom_shouldReturnNotFoundIfRoomDoesNotExist() {
        // Arrange
        val roomCode = "invalidCode"
        `when`(mockRoomsService.findRoomByCode(roomCode)).thenReturn(null)

        // Act
        val result = controller.deleteRoom(roomCode)

        // Assert
        assertEquals(HttpStatusCode.NotFound, result.first)
        assertEquals("Room not found", result.second)
    }

    @Test
    fun deleteRoom_shouldReturnBadRequestIfCodeIsNull() {
        // Act
        val result = controller.deleteRoom(null)

        // Assert
        assertEquals(HttpStatusCode.BadRequest, result.first)
        assertEquals("Missing or incorrect room code", result.second)
    }

}
