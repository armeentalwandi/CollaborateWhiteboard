import kotlinx.serialization.Serializable

@Serializable
data class Room (
    val roomId: String,
    var users: List<User> = listOf()
)