package composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import models.Line

@Composable
fun Whiteboard(selectedMode: String = "DRAW_LINES", color: Color = Color.Black, /*strokeWidth: Float=1f,*/ shape: Shape? = null) {
    var strokeSize by remember { mutableStateOf(1f) } // Define slider value here
    val lines = remember { mutableStateListOf<Line>() }
    var canvasSize by remember { mutableStateOf(Size(0f, 0f)) }

    println("Selected Mode: $selectedMode")

    // Slider
    Slider(
        value = strokeSize,
        valueRange = 1f..20f,
        onValueChange = { newValue ->
            strokeSize = newValue
//                        println(strokeSize)
        }
    )

    Canvas(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged { size ->
            canvasSize = size.toSize()
        }
        .background(Color.White)
        .pointerInput(selectedMode) {
                            detectDragGestures { change, dragAmount ->
//                                change.consume()
                                if (selectedMode == "DRAW_LINES") {
                                    val startPosition = change.position - dragAmount
                                    val endPosition = change.position

                                    // Check if the line is within the canvas bounds
                                    if (isWithinCanvasBounds(startPosition, canvasSize) && isWithinCanvasBounds(
                                            endPosition,
                                            canvasSize
                                        )
                                    ) {
                                        val line = Line(
                                            color = color,
                                            startOffset = startPosition,
                                            endOffset = endPosition,
                                            strokeWidth = strokeSize.toDp()
                                        )
                                        lines.add(line)
                                    }
                                } else if (selectedMode == "ERASE") {
                                    // ERASE LOGIC
                                } else if (selectedMode == "DRAW_SHAPES") {
                                    // DRAW A SHAPE LOGIC
                                } else if (selectedMode == "SELECT_LINES") {
                                    // SELECT ANYTHING WITHIN THE BOUNDS OF THE SELECTION
                                }
                            }
        },
    ) {

        lines.forEach {line ->
            drawLine(
                color = color,
                start = line.startOffset,
                end = line.endOffset,
                strokeWidth = line.strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

private fun isWithinCanvasBounds(offset: Offset, canvasSize: Size): Boolean {
    return offset.x >= 0f && offset.x <= canvasSize.width && offset.y >= 0f && offset.y <= canvasSize.height
}