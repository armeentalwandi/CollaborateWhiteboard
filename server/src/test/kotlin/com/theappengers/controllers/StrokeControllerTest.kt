package com.theappengers.controllers

import SerializableLine
import SerializableStroke
import StrokesService
import UpdateStrokesRequest
import com.theappengers.schemas.StrokesTable
import com.theappengers.schemas.UpdateStrokeRequest
import com.theappengers.schemas.createStroke
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.lang.reflect.Field
import java.util.*

class StrokeControllerTest {

    private lateinit var controller: StrokeController
    private lateinit var mockStrokesService : StrokesService
    private lateinit var serializedStroke : String
    private lateinit var stroke : SerializableStroke

    @BeforeEach
    fun setUp() {
        controller = StrokeController()
        mockStrokesService = mock(StrokesService::class.java)

        // Inject mock using reflection
        val field: Field = StrokeController::class.java.getDeclaredField("strokesService")
        field.isAccessible = true
        field.set(controller, mockStrokesService)

        // Arrange
        serializedStroke = Json.encodeToString(SerializableStroke(
            startOffset = Pair(350.0f, 374.0f),
            endOffset = Pair(354.0f, 378.0f),
            userId = "b568b4e3-3215-4431-be5d-90cf130a7d71",
            strokeId = "ea085334-d9e8-452d-bccf-115bf8a69eda",
            roomId = "c752b764-37a4-4476-b754-42cd0016a437",
            color = "#FF000000",
            serializableLines = mutableListOf(
                SerializableLine(0, Pair(348.25f, 374.0f), Pair(350.0f, 374.0f), "#FFFF0000", 0.5f),
                SerializableLine(1, Pair(350.0f, 374.0f), Pair(350.0f, 376.0f), "#FFFF0000", 0.5f),
                SerializableLine(2, Pair(350.0f, 376.0f), Pair(352.0f, 376.0f), "#FFFF0000", 0.5f),
                SerializableLine(3, Pair(352.0f, 376.0f), Pair(354.0f, 376.0f), "#FFFF0000", 0.5f),
                SerializableLine(4, Pair(354.0f, 376.0f), Pair(354.0f, 378.0f), "#FFFF0000", 0.5f)
            ),
            center = null
        ))
        stroke = Json.decodeFromString<SerializableStroke>(serializedStroke)
    }

    @AfterEach
    fun tearDown() {
        reset(mockStrokesService)
    }

    @Test
    fun createStroke_shouldReturnOKonSuccess() {
        // Arrange
        `when`(mockStrokesService.createStroke(stroke, serializedStroke)).thenAnswer {  }

        // Act
        val result = controller.createStroke(stroke, serializedStroke)

        // Assert
        verify(mockStrokesService).createStroke(stroke, serializedStroke)
        assertEquals(HttpStatusCode.OK, result.first)
        assertEquals("Stroke added successfully", result.second)
    }

    @Test
    fun createStroke_shouldReturnInternalServerErroronFailure() {

        `when`(mockStrokesService.createStroke(stroke, serializedStroke)).thenAnswer { throw Exception() }

        // Act
        val result = controller.createStroke(stroke, serializedStroke)

        // Assert
        verify(mockStrokesService).createStroke(stroke, serializedStroke)
        assertEquals(HttpStatusCode.InternalServerError, result.first)
        assertEquals("Something went wrong!", result.second)
    }


    @Test
    fun getRoomStrokes_shouldReturnOKIfNoDatabaseError() {
        // Arrange
        val roomId = UUID.randomUUID().toString()
        `when`(mockStrokesService.getRoomStrokes(roomId)).thenAnswer { listOf(serializedStroke) }

        // Act
        val result = controller.getRoomStrokes(roomId)

        // Assert
        assertEquals(HttpStatusCode.OK, result.first)
        assertEquals(listOf(serializedStroke), result.second)
    }

    @Test
    fun getRoomStrokes_shouldReturnBadRequestOnDatabaseError() {
        // Arrange
        val roomId = UUID.randomUUID().toString()
        `when`(mockStrokesService.getRoomStrokes(roomId)).thenAnswer { throw Exception() }

        // Act
        val result = controller.getRoomStrokes(roomId)

        // Assert
        assertEquals(HttpStatusCode.BadRequest, result.first)
        assertEquals("Invalid Request!", result.second)
    }

    @Test
    fun deleteStroke_shouldReturnOKIfNoDatabaseError() {
        // Arrange
        val strokeId = UUID.randomUUID().toString()
        `when`(mockStrokesService.deleteStroke(strokeId)).thenAnswer {}

        // Act
        val result = controller.deleteStroke(strokeId)

        // Assert
        assertEquals(HttpStatusCode.OK, result.first)
        assertEquals("Stroke deleted successfully", result.second)
    }

    @Test
    fun deleteStroke_shouldReturnBadRequestIfStrokeIdIsNull() {
        // Act
        val result = controller.deleteStroke(null)

        // Assert
        assertEquals(HttpStatusCode.BadRequest, result.first)
        assertEquals("Invalid or missing strokeId parameter", result.second)
    }

    @Test
    fun updateStrokes_shouldReturnOKIfStrokesUpdatedSuccessfully() {
        // Arrange
        val updateStrokesRequest = UpdateStrokesRequest(listOf(stroke))
        `when`(mockStrokesService.updateStrokeRow(UpdateStrokeRequest(UUID.fromString(stroke.strokeId), serializedStroke))).thenAnswer {}

        // Act
        val result = controller.updateStrokes(updateStrokesRequest)

        // Assert
        assertEquals(HttpStatusCode.OK, result.first)
        assertEquals("Strokes updated successfully", result.second)
    }

    @Test
    fun updateStrokes_shouldReturnInternalServerErrorIfException() {
        // Arrange
        val updateStrokesRequest = UpdateStrokesRequest(listOf(stroke))
        `when`(mockStrokesService.updateStrokeRow(UpdateStrokeRequest(UUID.fromString(stroke.strokeId), serializedStroke)))
            .thenAnswer {throw Exception()}

        // Act
        val result = controller.updateStrokes(updateStrokesRequest)

        // Assert
        assertEquals(HttpStatusCode.InternalServerError, result.first)
        assertEquals("Something went wrong!", result.second)
    }
}
