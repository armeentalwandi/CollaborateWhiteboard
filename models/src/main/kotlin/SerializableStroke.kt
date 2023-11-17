import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class SerializableStroke(
    var startOffset: Pair<Float, Float> = Pair(0f,0f),
    var endOffset: Pair<Float, Float> = Pair(0f,0f),
    var userId: String,
    var strokeId: String,
    var roomId: String,
    var color: String = "#000000",
    val serializableLines: MutableList<SerializableLine>,
    val center: Pair<Float, Float>? = null
)
