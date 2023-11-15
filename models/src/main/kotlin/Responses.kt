import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val token: String, val user: User?)

@Serializable
data class RegisterResponse(val token: String)