package models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

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