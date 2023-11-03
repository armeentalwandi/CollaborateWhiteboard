import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class ApiClient {

    private val baseUrl = "http://127.0.0.1:8080"
    private val client = HttpClient()

    // Replace with your actual server API endpoint
    suspend fun loginRequest(email: String, password: String): String {
        val url = "$baseUrl/auth/login"

        // Create a LoginRequest object with email and password
        val loginRequest = LoginRequest(email, password)

        return try{
            val response = client.post(url) {
                setBody(Json.encodeToString(loginRequest))
            }
            Json.decodeFromString<LoginResponse>(response.body()).token
        } catch(e: Exception) {
            "Invalid Credentials"
        }
    }

    suspend fun getAllStrokes(): List<SerializableStroke> {
        val url = "$baseUrl/strokes/all"
        val response = client.get(url)

        // Step 1: Decode the outer JSON to get a List<String>
        val jsonStringList: List<String> = Json.decodeFromString(response.body())
        val result = jsonStringList.map { Json.decodeFromString<SerializableStroke>(it) }
        println(result)

        // Step 2: Map over the list and decode each string to get SerializableStroke
        return jsonStringList.map { Json.decodeFromString<SerializableStroke>(it) }
    }


    suspend fun getStrokes(userId: UUID): List<SerializableStroke> {
        val url = "$baseUrl/strokes"
        val response = client.get(url) {
            url {
               parameters.append("user", userId.toString())
            }
        }
        return response.body() // all serializable strokes for the given user
    }

    suspend fun postStroke(stroke: SerializableStroke): HttpResponse {
        val url = "$baseUrl/strokes"
        val response = client.post(url) {
            setBody(Json.encodeToString(stroke))
        }
        return response
    }

    suspend fun deleteStroke(strokeId: UUID): HttpResponse {
        val url = "$baseUrl/strokes/$strokeId"
        return client.delete(url)
    }

}