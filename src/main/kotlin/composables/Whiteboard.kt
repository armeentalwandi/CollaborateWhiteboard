package composables

import ColorPicker
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import models.Line
import models.Stroke
import java.util.UUID

@Composable
fun Whiteboard(selectedMode: String = "DRAW_LINES", color: Color = Color.Black, shape: Shape? = null) {
    var strokeSize by remember { mutableStateOf(1f) } // Define slider value here
    var colour by remember { mutableStateOf(Color.Red) }

    val lines = remember { mutableStateListOf<Line>() }
    val strokes = remember { mutableStateListOf<Stroke>() }
    strokes.forEach {
        println(it)
    }

    var currentStroke: Stroke? by remember { mutableStateOf(null) }

    var canvasSize by remember { mutableStateOf(Size(0f, 0f)) }
    var colourPickerDialog: Boolean by remember { mutableStateOf(false) }

    println("Selected Mode: $selectedMode")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.weight(1f)) // Dynamic spacing to push the slider to the left

        Slider(
            value = strokeSize,
            valueRange = 1f..20f,
            onValueChange = { newValue ->
                strokeSize = newValue
            },
            modifier = Modifier
                .weight(3f) // Take up one-third of the available space
        )

        Spacer(modifier = Modifier.weight(1f)) // Dynamic spacing between the slider and the row

        TextButton(
            onClick = {
                  println("Colour Button Clicked")
                    colourPickerDialog = true
            },
            modifier = Modifier.background(colour).weight(1f)
        ) {}

        Spacer(modifier = Modifier.weight(1f)) // Dynamic spacing to push the row to the right
    }

    Spacer(modifier = Modifier.height(16.dp)) // Add spacing between the row and the canvas

    Canvas(modifier = Modifier
        .fillMaxSize()
        .onSizeChanged { size ->
            canvasSize = size.toSize()
        }
        .background(Color.White)
        .pointerInput(selectedMode) {
                            detectDragGestures (
                                onDragStart = { offset ->
                                    if (selectedMode == "DRAW_LINES") {
                                        currentStroke = Stroke(offset, userId = UUID.randomUUID(), lines = mutableListOf())
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val startPosition = change.position - dragAmount
                                    val endPosition = change.position
                                    if (isWithinCanvasBounds(startPosition, canvasSize) && isWithinCanvasBounds(endPosition, canvasSize)) {
                                        if (selectedMode == "DRAW_LINES") {
                                            // Check if the line is within the canvas bounds
                                                val line = Line(
                                                    id = if (lines.size > 0) { lines.last().id + 1 } else {0},
                                                    color = colour,
                                                    startOffset = startPosition,
                                                    endOffset = endPosition,
                                                    strokeWidth = strokeSize.toDp()
                                                )
                                                currentStroke?.lines?.add(line)
                                                lines.add(line)

                                        } else if (selectedMode == "ERASE") {
                                            // ERASE LOGIC

                                            // Find if any stroke has overlapping lines.
                                            var eraseableStroke: Stroke? = null
                                            for (stroke in strokes) {
                                                for (line in stroke.lines) {
                                                    if (doLinesIntersect(startPosition, endPosition, line.startOffset, line.endOffset)) {
                                                        eraseableStroke = stroke
                                                        break
                                                    }
                                                }
                                            }

                                            if (eraseableStroke != null) {
                                                strokes.remove(eraseableStroke)
                                                eraseableStroke.lines.forEach {
                                                    lines.remove(it)
                                                }
                                            }

                                        } else if (selectedMode == "DRAW_SHAPES") {
                                            // DRAW A SHAPE LOGIC
                                        } else if (selectedMode == "SELECT_LINES") {
                                            // SELECT ANYTHING WITHIN THE BOUNDS OF THE SELECTION
                                        }
                                    }
                                },
                                onDragEnd = {
                                    if (selectedMode == "DRAW_LINES") {
                                        currentStroke?.endOffset = currentStroke?.lines?.last()?.endOffset!!
                                        strokes.add(currentStroke!!)
                                        currentStroke = null
                                    }
                                }
                            )
        },
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

    if (colourPickerDialog) {
        Dialog(
            onDismissRequest = {
                colourPickerDialog = false // This callback is automatically called when the user dismisses the dialog.
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Column(
                modifier = Modifier.background(Color.White) // Set dialog background color
            ) {
                ColorPicker(color, onColorSelected = { selectedColour ->
                    colour = selectedColour
                    colourPickerDialog = false
                })
            }
        }
    }

}

private fun doLinesIntersect(line1start: Offset, line1end: Offset, line2start: Offset, line2end: Offset): Boolean {
    // Calculate the vectors representing the line segments
    val vector1 = line1end - line1start
    val vector2 = line2end - line2start

    // Calculate the cross product of the two vectors
    val crossProduct1 = (line2start.y - line1start.y) * vector1.x - (line2start.x - line1start.x) * vector1.y
    val crossProduct2 = (line2start.y - line1start.y) * vector2.x - (line2start.x - line1start.x) * vector2.y

    // Calculate the dot product of the two vectors
    val dotProduct1 = (line2start.x - line1start.x) * vector1.x + (line2start.y - line1start.y) * vector1.y
    val dotProduct2 = (line2end.x - line1start.x) * vector1.x + (line2end.y - line1start.y) * vector1.y

    // Check if the line segments are collinear (dot product is negative)
    if (dotProduct1 < 0 || dotProduct2 < 0) {
        return false
    }

    // Check if the line segments overlap
    if (crossProduct1 * crossProduct2 >= 0) {
        return false
    }

    return true
}


private fun isWithinCanvasBounds(offset: Offset, canvasSize: Size): Boolean {
    return offset.x >= 0f && offset.x <= canvasSize.width && offset.y >= 0f && offset.y <= canvasSize.height
}