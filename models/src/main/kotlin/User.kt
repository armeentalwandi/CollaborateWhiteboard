import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class User(
    val userId: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val auth_level: String,
    var rooms: List<Room> = listOf()
)