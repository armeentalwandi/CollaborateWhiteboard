import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomData(
    var roomName: String,
    var roomCode: String,
    var createdBy: String,
)