import kotlinx.serialization.Serializable


@Serializable
data class SerializableLine(
    val id: Int = 0,
    val startOffset: Pair<Float, Float>,
    val endOffset: Pair<Float, Float>,
    val color: String = "#OOOOOO",
    val strokeWidth: Float = 1f
)
