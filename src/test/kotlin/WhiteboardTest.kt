import androidx.compose.ui.geometry.Offset
import kotlin.test.Test
import composables.Whiteboard
import org.junit.jupiter.api.Assertions.assertEquals

class WhiteboardTest {
    @Test

    fun testWhiteboardDrawLines() {
        // Create a Whiteboard instance (you may need to pass parameters as needed)
        val whiteboard = Whiteboard(selectedMode = "DRAW_LINES")

        // Simulate drawing a line
        val startPoint = Offset(10f, 10f)
        val endPoint = Offset(20f, 20f)
        val lineColor = Color.Black
        val strokeWidth = 2f
        whiteboard.drawLine(startPoint, endPoint, lineColor, strokeWidth)

        // Get the lines drawn on the Whiteboard
        val lines = whiteboard.getLines()

        // Assert that the Whiteboard contains the expected line
        assertEquals(1, lines.size) // Assuming only one line is drawn
        val drawnLine = lines[0]
        assertEquals(startPoint, drawnLine.startOffset)
        assertEquals(endPoint, drawnLine.endOffset)
        assertEquals(lineColor, drawnLine.color)
        assertEquals(strokeWidth, drawnLine.strokeWidth, 0.001f) // Tolerance for floating-point comparison
        assertEquals(StrokeCap.Round, drawnLine.cap) // Assuming you're using Round cap
    }





}