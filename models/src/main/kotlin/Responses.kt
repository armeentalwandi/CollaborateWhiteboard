import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val token: String)

@Serializable
data class RegisterResponse(val token: String)