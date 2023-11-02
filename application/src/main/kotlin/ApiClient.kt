import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

class ApiClient {

    private val baseUrl = "http://127.0.0.1:8080"
    private val client = HttpClient()


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