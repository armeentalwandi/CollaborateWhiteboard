package composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import models.Line

@Composable
fun Whiteboard() {

    val lines = remember { mutableStateListOf<Line>() }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .pointerInput(true) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val line = Line(
                                    startOffset = change.position - dragAmount,
                                    endOffset = change.position
                                )

                                lines.add(line)
                            }
        },
//        onDraw = {
//            // Sample Drawings
//            drawCircle(Color.Blue, 50f)
//            drawLine(Color.Magenta, Offset(10f, 10f), Offset(200f, 200f))
//        }

    ) {
        lines.forEach {line ->
            drawLine(
                color = line.color,
                start = line.startOffset,
                end = line.endOffset,
                strokeWidth = line.strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}