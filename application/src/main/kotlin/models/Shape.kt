package models

import TEMP_UUID
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.*
import kotlin.math.*

fun createCircleStroke(center: Offset, radius: Float, colour:Color, strokeSize: Float, segments: Int = 360): Stroke {
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
        userId = TEMP_UUID,
        strokeId = UUID.randomUUID().toString(),
        lines = lines,
        center = center
    )
}

fun createRectangleStroke(topLeft: Offset, bottomRight: Offset, colour: Color, strokeSize: Float): Stroke {
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
        userId = TEMP_UUID,
        strokeId = UUID.randomUUID().toString(),
        color = colour,
        lines = lines
    )
}


fun createTriangleStroke(vertex1: Offset, endOffset: Offset, colour: Color, strokeSize: Float): Stroke {
    println("Start $vertex1")
    println("End $endOffset")

    val lines = mutableListOf<Line>()

    // Calculate the side length from the distance between vertex1 and endOffset
    val sideLength = sqrt((vertex1.x - endOffset.x).pow(2) + (vertex1.y - endOffset.y).pow(2))

    // Calculate the third vertex of the equilateral triangle
    val dx = endOffset.x - vertex1.x
    val dy = endOffset.y - vertex1.y
    val perpendicularDx = -dy
    val perpendicularDy = dx

    val normalizedPerpDx = perpendicularDx / sqrt(perpendicularDx.pow(2) + perpendicularDy.pow(2))
    val normalizedPerpDy = perpendicularDy / sqrt(perpendicularDx.pow(2) + perpendicularDy.pow(2))

    val halfHeight = (sqrt(3.0) / 2 * sideLength).toFloat()
    val midPointX = (vertex1.x + endOffset.x) / 2
    val midPointY = (vertex1.y + endOffset.y) / 2

    val vertex3 = Offset(midPointX + halfHeight * normalizedPerpDx, midPointY + halfHeight * normalizedPerpDy)

    // Line from vertex1 to endOffset
    lines.add(Line(startOffset = vertex1, endOffset = endOffset, color = colour, strokeWidth = strokeSize.dp))

    // Line from endOffset to vertex3
    lines.add(Line(startOffset = endOffset, endOffset = vertex3, color = colour, strokeWidth = strokeSize.dp))

    // Line from vertex3 to vertex1
    lines.add(Line(startOffset = vertex3, endOffset = vertex1, color = colour, strokeWidth = strokeSize.dp))

    return Stroke(
        startOffset = vertex1,
        endOffset = vertex1,  // As per your definition for rectangles
        userId = TEMP_UUID,
        strokeId = UUID.randomUUID().toString(),
        color = colour,
        lines = lines
    )
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