package models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.util.*
import kotlin.test.assertEquals

class StrokeKtTest {
    private lateinit var testStroke: Stroke
    private lateinit var testLine: Line
    private var testColor = Color.Red

    @BeforeEach
    fun setUp() {
        val testOffsetStart = Offset(10f, 20f)
        val testOffsetEnd = Offset(30f, 40f)
        testColor = Color.Red
        testLine = Line(
            id = UUID.randomUUID().hashCode(),
            startOffset = testOffsetStart,
            endOffset = testOffsetEnd,
            color = testColor,
            strokeWidth = 5.dp
        )
        testStroke = Stroke(
            startOffset = testOffsetStart,
            endOffset = testOffsetEnd,
            userId = "user123",
            strokeId = UUID.randomUUID().toString(),
            roomId = "room123",
            color = testColor,
            lines = mutableListOf(testLine),
            center = testOffsetStart
        )
    }

    @Test
    fun toSerializable() {
        val serializedStroke = toSerializable(testStroke)
        assertAll(
            { assertEquals(testStroke.startOffset.x, serializedStroke.startOffset.first) },
            { assertEquals(testStroke.startOffset.y, serializedStroke.startOffset.second) },
        )
    }

    @Test
    fun testToSerializable() {
        val serializedLine = toSerializable(testLine)
        assertAll(
            { assertEquals(testLine.startOffset.x, serializedLine.startOffset.first) },
            { assertEquals(testLine.startOffset.y, serializedLine.startOffset.second) },
        )
    }

    @Test
    fun fromSerializable() {
        val serializedStroke = toSerializable(testStroke)
        val deserializedStroke = fromSerializable(serializedStroke)
        assertAll(
            { assertEquals(testStroke.startOffset, deserializedStroke.startOffset) },
            { assertEquals(testStroke.endOffset, deserializedStroke.endOffset) },
        )
    }

    @Test
    fun testFromSerializable() {
        val serializedLine = toSerializable(testLine)
        val deserializedLine = fromSerializable(serializedLine)
        assertAll(
            { assertEquals(testLine.startOffset, deserializedLine.startOffset) },
            { assertEquals(testLine.endOffset, deserializedLine.endOffset) },
        )
    }

    @Test
    fun toHex() {
        val hex = testColor.toHex()
        assertEquals("#FFFF0000", hex)
    }

    @Test
    fun toColor() {
        val color = "#FFFF0000".toColor()
        assertEquals(testColor, color)
    }
}