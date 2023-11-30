import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*


class ApiClient {

    // Base URL for the API
    private val baseUrl = if (ENVIRONMENT != "remote") "http://127.0.0.1:8080" else "https://gothic-depth-405218.uc.r.appspot.com/"

    // HTTP client used for making requests
    private val client = HttpClient()

    // Function to get all strokes from the server
    suspend fun getAllStrokes(roomId: UUID): List<SerializableStroke> {
        val url = "$baseUrl/strokes/all/${roomId}"
        val response = client.get(url)

        // Decode the outer JSON to get a List<String>
        val jsonStringList: List<String> = Json.decodeFromString(response.body())

        // Map over the list and decode each string to get SerializableStroke
        return jsonStringList.map { Json.decodeFromString<SerializableStroke>(it) }
    }

    // Function to get strokes for a specific user
    suspend fun getStrokes(userId: UUID): List<SerializableStroke> {
        val url = "$baseUrl/strokes"
        val response = client.get(url) {
            url {
               parameters.append("user", userId.toString())
            }
        }
        return response.body() // all serializable strokes for the given user
    }

    // Function to post a stroke to the server
    suspend fun postStroke(stroke: SerializableStroke): HttpResponse {
        val url = "$baseUrl/strokes"
        val response = client.post(url) {
            setBody(Json.encodeToString(stroke))
        }
        return response
    }

    // Function to delete a stroke by its ID
    suspend fun deleteStroke(strokeId: UUID): HttpResponse {
        val url = "$baseUrl/strokes/$strokeId"
        return client.delete(url)
    }
    suspend fun updateStrokes(serializedStrokes: List<SerializableStroke>) {
        val url = "$baseUrl/strokes/update"
        val updateStrokesRequest = UpdateStrokesRequest(serializedStrokes)
        try {
            val response = client.put(url) {
                setBody(Json.encodeToString(updateStrokesRequest))
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    // Function to send a login request to the server
    suspend fun loginRequest(email: String, password: String): Pair<String, User?> {
        val url = "$baseUrl/auth/login"

        // Create a LoginRequest object with email and password
        val loginRequest = LoginRequest(email, password)

        return try{
            // Send a POST request with the login request
            val response = client.post(url) {
                setBody(Json.encodeToString(loginRequest))
            }

            // Decode the response to get the login token
            val decoded = Json.decodeFromString<LoginResponse>(response.body())
            Pair(decoded.token, decoded.user)
        } catch(e: Exception) {
            // Handle exceptions (e.g., invalid credentials)
            Pair("Invalid Credentials", null)
        }
    }

    // Function to send a registration request to the server
    suspend fun registerRequest(email: String, password: String, firstName: String, lastName: String, role: String): Any {
        val url = "$baseUrl/auth/register"

        // Create a LoginRequest object with email and password
        val registerRequest = RegisterRequest(email, password, firstName, lastName, role)

        return try{
            // Send a POST request with the registration request
            val response = client.post(url) {
                setBody(Json.encodeToString(registerRequest))
            }
            // Decode the response to get the login token
            Json.decodeFromString<RegisterResponse>(response.body()).token
        } catch(e: Exception) {
            "Invalid Credentials"
        }
    }

    // Function to get all rooms for a specific user
    suspend fun getUserRooms(userId: String): List<Room> {
        val url = "$baseUrl/rooms/user/$userId"
        val response = client.get(url)
        return Json.decodeFromString(response.body())
    }

    suspend fun removeUserFromRoom(roomId: String, userId: String): HttpResponse {
        val url = "$baseUrl/rooms/room/$roomId/user/$userId"
        return client.delete(url)
    }

    suspend fun createRoom(roomName: String, roomCode: String, createdBy: UUID): HttpResponse {
        val url = "$baseUrl/rooms/create"
        val roomData = RoomData(roomName, roomCode, createdBy.toString())
        return client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(roomData))
        }
    }
    suspend fun deleteRoom(roomCode: String): HttpResponse {
        val url = "$baseUrl/rooms/delete/$roomCode"
        return client.delete(url)
    }

    suspend fun findRoomByCode(roomCode: String): Room? {
        val url = "$baseUrl/rooms/room/$roomCode"
        val response = client.get(url)
        when (response.status) {
            HttpStatusCode.OK -> {
                return Json.decodeFromString<Room>(response.body())
            }
            HttpStatusCode.NotFound -> {
                // Room not found, return null
                return null
            }
            HttpStatusCode.BadRequest -> {
                // Invalid room code, throw an exception
                throw IllegalArgumentException("Invalid or missing roomCode")
            }
            else -> {
                // Handle other unexpected status codes
                throw RuntimeException("Unexpected response: ${response.status.description}")
            }
        }
    }
}