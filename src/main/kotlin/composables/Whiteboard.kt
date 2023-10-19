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
                                                    if (doLinesCross(startPosition, endPosition, line.startOffset, line.endOffset)) {
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

private fun doLinesCross(line1start: Offset, line1end: Offset, line2start: Offset, line2end: Offset): Boolean {
    // Calculate the direction of the lines
    val a1 = line1end.y - line1start.y
    val b1 = line1start.x - line1end.x
    val c1 = a1 * line1start.x + b1 * line1start.y

    val a2 = line2end.y - line2start.y
    val b2 = line2start.x - line2end.x
    val c2 = a2 * line2start.x + b2 * line2start.y

    val determinant = a1 * b2 - a2 * b1

    // If the determinant is 0, the lines are parallel and won't intersect
    if (determinant == 0f) {
        return false
    }

    // Calculate the intersection point
    val intersectX = (b2 * c1 - b1 * c2) / determinant
    val intersectY = (a1 * c2 - a2 * c1) / determinant

    // Check if the intersection point lies within both line segments
    if (intersectX in minOf(line1start.x, line1end.x)..maxOf(line1start.x, line1end.x) &&
        intersectY in minOf(line1start.y, line1end.y)..maxOf(line1start.y, line1end.y) &&
        intersectX in minOf(line2start.x, line2end.x)..maxOf(line2start.x, line2end.x) &&
        intersectY in minOf(line2start.y, line2end.y)..maxOf(line2start.y, line2end.y)) {
        return true
    }

    return false
}



private fun isWithinCanvasBounds(offset: Offset, canvasSize: Size): Boolean {
    return offset.x >= 0f && offset.x <= canvasSize.width && offset.y >= 0f && offset.y <= canvasSize.height
}