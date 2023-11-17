import kotlinx.serialization.Serializable

@Serializable
class UpdateStrokesRequest(val serializedStrokes: List<SerializableStroke>) {

}
