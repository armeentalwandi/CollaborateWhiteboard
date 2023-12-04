package com.theappengers.controllers

import SerializableStroke
import StrokesService
import UpdateStrokesRequest
import com.theappengers.schemas.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class StrokeController {

    private val strokesService = StrokesService()

    fun createStroke(serializableStroke: SerializableStroke, serialized: String): Pair<HttpStatusCode, String> {
        return try {
            strokesService.createStroke(serializableStroke, serialized)
            HttpStatusCode.OK to "Stroke added successfully"
        } catch (e: Exception) {
            HttpStatusCode.InternalServerError to "Something went wrong!"
        }
    }

    fun getRoomStrokes(roomId: String?): Pair<HttpStatusCode, Any> {
        return try {
            val allStrokes = strokesService.getRoomStrokes(roomId)
            HttpStatusCode.OK to allStrokes
        } catch (e: Exception) {
            HttpStatusCode.BadRequest to "Invalid Request!"
        }
    }

    fun deleteStroke(strokeId: String?): Pair<HttpStatusCode, String> {
        return if (strokeId != null) {
            strokesService.deleteStroke(strokeId)
            HttpStatusCode.OK to "Stroke deleted successfully"
        } else {
            HttpStatusCode.BadRequest to "Invalid or missing strokeId parameter"
        }
    }

    fun updateStrokes(updateStrokesRequest: UpdateStrokesRequest): Pair<HttpStatusCode, String> {
        return try {
            updateStrokesRequest.serializedStrokes.forEach { stroke ->
                    val strokeString = Json.encodeToString(stroke)
                strokesService.updateStrokeRow(UpdateStrokeRequest(UUID.fromString(stroke.strokeId), strokeString))
            }
            HttpStatusCode.OK to "Strokes updated successfully"
        } catch (e: Exception) {
            HttpStatusCode.InternalServerError to "Something went wrong!"
        }
    }
}
