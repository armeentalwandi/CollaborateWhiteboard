package models

import User
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.*
import kotlin.math.*

fun createCircleStroke(
    center: Offset,
    initialRadius: Float,
    colour: Color,
    strokeSize: Float,
    canvasSize: Size,
    segments: Int = 360,
    appData: AppData
): Stroke {
    // Calculate the maximum radius that fits within the canvas from the center point
    val maxXRadius = minOf(center.x, canvasSize.width - center.x)
    val maxYRadius = minOf(center.y, canvasSize.height - center.y)
    val maxRadius = minOf(maxXRadius, maxYRadius) - strokeSize / 2  // Subtract half stroke size to account for line thickness

    // Adjust the radius so it does not go beyond the canvas edges
    val radius = minOf(initialRadius, maxRadius)

    val angleIncrement = 2 * PI / segments
    val lines = mutableListOf<Line>()

    for (i in 0 until segments) {
        val startAngle = i * angleIncrement
        val endAngle = (i + 1) * angleIncrement

        val startX = center.x + radius * cos(startAngle).toFloat()
        val startY = center.y + radius * sin(startAngle).toFloat()
        val startOffset = Offset(startX, startY)

        val endX = center.x + radius * cos(endAngle).toFloat()
        val endY = center.y + radius * sin(endAngle).toFloat()
        val endOffset = Offset(endX, endY)

        val line = Line(
            id = i,
            color = colour,
            startOffset = startOffset,
            endOffset = endOffset,
            strokeWidth = strokeSize.dp
        )
        lines.add(line)
    }

    return Stroke(
        color = colour,
        startOffset = lines.first().startOffset,
        endOffset = lines.last().endOffset,
        userId = appData.user!!.userId,
        strokeId = UUID.randomUUID().toString(),
        roomId = appData.currRoom!!.roomId,
        lines = lines,
        center = center
    )
}
fun createRectangleStroke(
    topLeft: Offset,
    bottomRight: Offset,
    colour: Color,
    strokeSize: Float,
    appData: AppData
): Stroke {
    val lines = mutableListOf<Line>()

    // Define the corners of the rectangle
    val topRight = Offset(bottomRight.x, topLeft.y)
    val bottomLeft = Offset(topLeft.x, bottomRight.y)

    // Top line
    lines.add(Line(strokeWidth = strokeSize.dp, startOffset = topLeft, endOffset = topRight, color = colour))

    // Right line
    lines.add(Line(strokeWidth = strokeSize.dp, startOffset = topRight, endOffset = bottomRight, color = colour))

    // Bottom line
    lines.add(Line(strokeWidth = strokeSize.dp, startOffset = bottomRight, endOffset = bottomLeft, color = colour))

    // Left line
    lines.add(Line(strokeWidth = strokeSize.dp, startOffset = bottomLeft, endOffset = topLeft, color = colour))

    return Stroke(
        startOffset = topLeft,
        endOffset = bottomRight,
        userId = appData.user!!.userId,
        strokeId = UUID.randomUUID().toString(),
        roomId = appData.currRoom!!.roomId,
        color = colour,
        lines = lines
    )
}

fun createTriangleStroke(
    vertex1: Offset,
    dragEnd: Offset,
    colour: Color,
    strokeSize: Float,
    canvasSize: Size,
    appData: AppData
): Stroke {
    val dragDirection = dragEnd - vertex1

    // Calculate the height of the triangle
    val height = sqrt(dragDirection.x * dragDirection.x + dragDirection.y * dragDirection.y)

    // Normalize the drag direction
    val dragNormalized = if (height == 0f) Offset(0f, 0f) else Offset(dragDirection.x / height, dragDirection.y / height)

    // Calculate the direction perpendicular to the drag direction
    val perpendicularDirection = Offset(-dragNormalized.y, dragNormalized.x)

    // Midpoint of the base
    val baseMidpoint = vertex1 + dragDirection

    // Half of the base length; adjust this value as per your needs (e.g., it can be a fraction of the height)
    val halfBaseLength = height / 2  // This ensures the triangle is isosceles

    // Calculate the two base vertices
    val baseVertex1 = Offset(
        (baseMidpoint.x + perpendicularDirection.x * halfBaseLength).coerceIn(0f, canvasSize.width.toFloat()),
        (baseMidpoint.y + perpendicularDirection.y * halfBaseLength).coerceIn(0f, canvasSize.height.toFloat())
    )

    val baseVertex2 = Offset(
        (baseMidpoint.x - perpendicularDirection.x * halfBaseLength).coerceIn(0f, canvasSize.width.toFloat()),
        (baseMidpoint.y - perpendicularDirection.y * halfBaseLength).coerceIn(0f, canvasSize.height.toFloat())
    )

    // Construct your triangle using these three vertices: vertex1, baseVertex1, and baseVertex2
    val lines = mutableListOf<Line>().apply {
        add(Line(startOffset = vertex1, endOffset = baseVertex1, color = colour, strokeWidth = strokeSize.dp))
        add(Line(startOffset = baseVertex1, endOffset = baseVertex2, color = colour, strokeWidth = strokeSize.dp))
        add(Line(startOffset = baseVertex2, endOffset = vertex1, color = colour, strokeWidth = strokeSize.dp))
    }

    return Stroke(
        startOffset = vertex1,
        endOffset = baseVertex2,
        userId = appData.user!!.userId,
        strokeId = UUID.randomUUID().toString(),
        roomId = appData.currRoom!!.roomId,
        color = colour,
        lines = lines
    )
}

fun withinBounds(offset: Offset, canvasSize: Size): Boolean {
    return offset.x in 0f..canvasSize.width && offset.y in 0f..canvasSize.height
}


sealed class Shape {
    abstract val color: Color
    abstract val position: Offset
    abstract val size: Size

    data class Rectangle(
        override val color: Color,
        override val position: Offset,
        override val size: Size
    ) : Shape()

    data class Circle(
        override val color: Color,
        override val position: Offset,
        override val size: Size
    ) : Shape()

    data class Triangle(
        override val color: Color,
        override val position: Offset,
        override val size: Size
    ) : Shape()
}

enum class ShapeType {
    Rectangle, Circle, Triangle
}