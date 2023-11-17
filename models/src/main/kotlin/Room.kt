import kotlinx.serialization.Serializable

@Serializable
data class Room(
    var roomId: String,
    var roomName: String,
    var roomCode: String,
    var createdBy: String,
    var users: List<User> = listOf()
//    var createdAt: LocalDateTime
)