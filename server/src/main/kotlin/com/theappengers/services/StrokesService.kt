import com.theappengers.schemas.*

class StrokesService {
    fun createStroke(stroke: SerializableStroke, serializedStroke: String) {
        StrokesTable.createStroke(stroke, serializedStroke)
    }

    fun getRoomStrokes(roomId: String?): List<String> {
        return StrokesTable.getRoomStrokes(roomId)
    }

    fun deleteStroke(strokeId: String) {
        StrokesTable.deleteStroke(strokeId)
    }

    fun updateStrokeRow(updateRequest: UpdateStrokeRequest) {
        StrokesTable.updateStrokeRow(updateRequest)
    }
}