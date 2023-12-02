package models

import Room
import User
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShapeTest {
    private lateinit var appData: AppData

    @BeforeEach
    fun setUp() {
        appData = AppData(
            user = User("userId", "email@example.com", "John", "Doe", "alias", listOf()),
            currRoom = Room("roomId", "roomName", "roomCode", "roomOwner")
        )
    }

    @Test
    fun circleStroke_hasCorrectProperties() {
        // Arrange
        val center = Offset(50f, 50f)
        val initialRadius = 40f
        val colour = Color.Red
        val strokeSize = 5f
        val canvasSize = Size(100f, 100f)
        val segments = 360

        // Act
        val stroke = createCircleStroke(center, initialRadius, colour, strokeSize, canvasSize, segments, appData)

        // Assert
        assertEquals(stroke.lines.size, segments, "Number of segments is incorrect")
        assertEquals(colour, stroke.color, "Stroke color is incorrect")
    }


    @Test
    fun createRectangleStroke_createsCorrectRectangle() {
        val topLeft = Offset(10f, 10f)
        val bottomRight = Offset(100f, 100f)
        val colour = Color.Red
        val strokeSize = 5f

        val stroke = createRectangleStroke(topLeft, bottomRight, colour, strokeSize, appData)

        assertEquals(4, stroke.lines.size)
        assertEquals(colour, stroke.color)
        assertTrue(stroke.lines.all { it.strokeWidth == strokeSize.dp })
        assertEquals(topLeft, stroke.lines[0].startOffset)
        assertEquals(bottomRight, stroke.lines[2].startOffset)
    }

    @Test
    fun createTriangleStroke_createsCorrectIsoscelesTriangle() {
        fun Offset.distanceTo(other: Offset): Float {
            return sqrt((x - other.x).pow(2) + (y - other.y).pow(2))
        }
        val vertex1 = Offset(50f, 50f)
        val dragEnd = Offset(150f, 50f)
        val colour = Color.Green
        val strokeSize = 5f
        val canvasSize = Size(200f, 200f)

        val stroke = createTriangleStroke(vertex1, dragEnd, colour, strokeSize, canvasSize, appData)

        val expectedHeight = sqrt((dragEnd.x - vertex1.x).pow(2) + (dragEnd.y - vertex1.y).pow(2))
        val expectedBaseLength = expectedHeight // Because it's isosceles and height is the same as half-base

        assertEquals(3, stroke.lines.size)
        assertEquals(colour, stroke.color)
        assertTrue(stroke.lines.all { it.strokeWidth == strokeSize.dp })
        assertEquals(expectedBaseLength, stroke.lines[1].startOffset.distanceTo(stroke.lines[1].endOffset))
    }
}

