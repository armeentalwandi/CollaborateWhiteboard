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

    suspend fun get_all_strokes(userId: UUID): List<SerializableStroke> {
        val url = "$baseUrl/strokes"
        val response = client.get(url) {
            url {
                parameters.append("user", userId.toString())
            }
        }
        return response.body()// all serializable strokes
    }

    suspend fun get_strokes(userId: UUID): List<SerializableStroke> {
        val url = "$baseUrl/strokes"
        val response = client.get(url) {
            url {
               parameters.append("user", userId.toString())
            }
        }
        return response.body()// all serializable strokes
    }

    suspend fun post_stroke(stroke: SerializableStroke): HttpResponse {
        val url = "$baseUrl/strokes"
        val response = client.post(url) {
            setBody(Json.encodeToString(stroke))
        }
        return response
    }

    suspend fun delete_stroke(stroke: SerializableStroke): HttpResponse {
        val url = "$baseUrl/strokes"
        val response = client.post(url) {
            setBody(stroke)
        }
        return response
    }

}