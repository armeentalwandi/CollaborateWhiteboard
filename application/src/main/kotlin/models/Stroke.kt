package models

import SerializableLine
import SerializableStroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import java.util.*


data class Stroke(
    var startOffset: Offset = Offset(0f, 0f),
    var endOffset: Offset = Offset(0f, 0f),
    var userId: String,
    var strokeId: String,
    var color: Color = Color.Black,
    val lines: MutableList<Line>,
    val center: Offset? = null
)

fun toSerializable(stroke: Stroke) : SerializableStroke {
    val serializable = SerializableStroke(
        Pair(stroke.startOffset.x, stroke.startOffset.y),
        Pair(stroke.endOffset.x, stroke.endOffset.y),
        stroke.userId,
        stroke.strokeId,
        stroke.color.toHex(),
        stroke.lines.map { toSerializable(it) }.toMutableList(),
        if (stroke.center != null) { Pair(stroke.center.x, stroke.center.y) } else { null }
        )
    return serializable
}

fun toSerializable(line: Line) : SerializableLine {
    val serializable = SerializableLine(
        line.id,
        Pair(line.startOffset.x, line.startOffset.y),
        Pair(line.endOffset.x, line.endOffset.y),
        line.color.toHex(),
        line.strokeWidth.value

    )
    return serializable
}

fun fromSerializable(serializableStroke: SerializableStroke): Stroke {
    val stroke = Stroke(
        Offset(serializableStroke.startOffset.first, serializableStroke.startOffset.second),
        Offset(serializableStroke.endOffset.first, serializableStroke.endOffset.second),
        serializableStroke.userId,
        serializableStroke.strokeId,
        serializableStroke.color.toColor(),
        serializableStroke.serializableLines.map { fromSerializable(it) }.toMutableList(),
        serializableStroke.center?.let { Offset(it.first, it.second) }
    )
    return stroke
}

fun fromSerializable(serializableLine: SerializableLine): Line {
    val line = Line(
        serializableLine.id,
        Offset(serializableLine.startOffset.first, serializableLine.startOffset.second),
        Offset(serializableLine.endOffset.first, serializableLine.endOffset.second),
        serializableLine.color.toColor(),
        serializableLine.strokeWidth.dp
    )
    return line
}

fun Color.toHex(): String {
    val argb = this.toArgb()
    val hexString = String.format("#%08X", argb)
    return hexString
}

fun String.toColor(): Color {
    // Remove the "#" character if present
    val hex = if (startsWith("#")) substring(1) else this

    // Parse the hexadecimal string to an integer
    val argb = try {
        hex.toLong(16).toInt()
    } catch (e: NumberFormatException) {
        // Handle invalid input here if needed
        // You may want to return a default color or throw an exception
        Color.Black.toArgb() // Default to black in case of invalid input
    }

    // Create a Color object from the ARGB integer
    return Color(argb)
}
