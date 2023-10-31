import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Stroke(
    var startOffset: Pair<Float, Float> = Pair(0f,0f),
    var endOffset: Pair<Float, Float> = Pair(0f,0f),
    var userId: UUID,
    var color: String = "#000000",
    val lines: MutableList<Line>,
    val center: Pair<Float, Float>? = null
)