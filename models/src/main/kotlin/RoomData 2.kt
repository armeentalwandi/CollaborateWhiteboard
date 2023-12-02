import kotlinx.serialization.Serializable

@Serializable
data class RoomData(
    var roomName: String,
    var roomCode: String,
    var createdBy: String,
)