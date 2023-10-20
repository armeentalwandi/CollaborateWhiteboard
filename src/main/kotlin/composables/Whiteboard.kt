package composables

import ColorPicker
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import models.*

import java.util.UUID
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun Whiteboard(selectedMode: String = "DRAW_LINES", shape: ShapeType? = null) {
    var strokeSize by remember { mutableStateOf(1f) } // Define slider value here
    var colour by remember { mutableStateOf(Color.Red) }
    val lines = remember { mutableStateListOf<Line>() }
    val strokes = remember { mutableStateListOf<Stroke>() }
    var currentStroke: Stroke? by remember { mutableStateOf(null) }
    var canvasSize by remember { mutableStateOf(Size(0f, 0f)) }
    var colourPickerDialog: Boolean by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<Action>() }
    val redoStack = remember { mutableStateListOf<Action>() }

    var showShapeOptionsScreen by remember { mutableStateOf(false) }
    var selectedShapeType by remember { mutableStateOf<ShapeType?>(null) }

    if (selectedMode == "DRAW_SHAPES" && selectedShapeType == null) {
        showShapeOptionsScreen = true
    }

    fun undo() {
        if (history.isNotEmpty()) {
            val lastAction = history.removeLast()
            when (lastAction) {
                is Action.AddStroke -> {
                    strokes.remove(lastAction.stroke)
                    lines.removeAll(lastAction.stroke.lines)
                }
                is Action.ChangeColor -> {
                    // Restore to the previous color or handle accordingly
                }
                // ... handle other action types
            }
            redoStack.add(lastAction)  // Push to redo stack
        }
    }


    println("Selected Mode: $selectedMode")
    fun redo() {
        if (redoStack.isNotEmpty()) {
            val actionToRedo = redoStack.removeLast()
            when (actionToRedo) {
                is Action.AddStroke -> {
                    strokes.add(actionToRedo.stroke)
                    lines.addAll(actionToRedo.stroke.lines)
                }
                is Action.ChangeColor -> {
                    // Apply the color change again
                }
                // ... handle other action types
            }
            history.add(actionToRedo)  // Push back to history
        }
    }


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Button(onClick = { undo() }, enabled = history.isNotEmpty()) {
            Text("Undo")
        }

        Spacer(modifier = Modifier.width(8.dp))  // Add some space between the buttons

        Button(onClick = { redo() }, enabled = redoStack.isNotEmpty()) {
            Text("Redo")
        }

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
                                    } else if (selectedMode == "DRAW_SHAPES") {
                                        if (selectedShapeType == ShapeType.Circle) {
                                            currentStroke = createCircleStroke(center = offset, radius=0f, colour = colour, strokeSize = strokeSize)
                                        } else if (selectedShapeType == ShapeType.Rectangle) {
                                            currentStroke = createRectangleStroke(topLeft = offset, bottomRight = offset, colour = colour, strokeSize = strokeSize)
                                        } else if (selectedShapeType == ShapeType.Triangle) {
                                            currentStroke = createTriangleStroke(vertex1 = offset, endOffset = offset, colour = colour, strokeSize = strokeSize)
                                        }
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
                                            if (selectedShapeType == ShapeType.Circle) {
                                                val radius = DistanceBetweenPoints(currentStroke!!.center!!, endPosition)
                                                currentStroke = createCircleStroke(currentStroke!!.center!!, radius, colour, strokeSize)
                                            } else if (selectedShapeType == ShapeType.Rectangle) {
                                                currentStroke = createRectangleStroke(topLeft = currentStroke!!.startOffset, bottomRight = endPosition, colour = colour, strokeSize = strokeSize)
                                            } else if (selectedShapeType == ShapeType.Triangle) {
                                                currentStroke = createTriangleStroke(vertex1 = currentStroke!!.startOffset, endOffset = endPosition, colour = colour, strokeSize = strokeSize)
                                            }
                                        } else if (selectedMode == "SELECT_LINES") {
                                            // SELECT ANYTHING WITHIN THE BOUNDS OF THE SELECTION
                                        }
                                    }
                                },
                                onDragEnd = {
                                    if (selectedMode == "DRAW_LINES") {
                                        currentStroke?.endOffset = currentStroke?.lines?.last()?.endOffset!!
                                        strokes.add(currentStroke!!)
                                        history.add(Action.AddStroke(currentStroke!!))
                                        redoStack.clear()  // Clear redo stack when a new action is done
                                        currentStroke = null
                                    } else if (selectedMode == "DRAW_SHAPES") {
                                        strokes.add(currentStroke!!)
                                        lines.addAll(currentStroke!!.lines)
                                        history.add(Action.AddStroke(currentStroke!!))
                                        redoStack.clear()  // Clear redo stack when a new action is done
                                        currentStroke = null
                                        selectedShapeType = null
                                    }
                                }
                            )
        },
    ) {

        currentStroke?.lines?.forEach { currStrokeLine ->
            println("${currStrokeLine.startOffset} ${currStrokeLine.endOffset}")
            drawLine(
                color = currStrokeLine.color,
                start = currStrokeLine.startOffset,
                end = currStrokeLine.endOffset,
                strokeWidth = currStrokeLine.strokeWidth.toPx(),
                cap = StrokeCap.Round
            )
        }

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
                ColorPicker(colour, onColorSelected = { selectedColour ->
                    colour = selectedColour
                    colourPickerDialog = false
                })
            }
        }
    } else if (showShapeOptionsScreen) {
        AlertDialog(
            onDismissRequest = { showShapeOptionsScreen = false },
            title = { Text("Select a Shape") },
            buttons = {
                // Display shape options in the dialog
                ShapeOptionsDialogButtons { selectedShape ->
                    // Handle the selected shape (e.g., set it in the Whiteboard composable)
                    selectedShapeType = selectedShape
                    showShapeOptionsScreen = false // Close the dialog
                }
            }
        )
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


private fun DistanceBetweenPoints(offset1: Offset, offset2: Offset): Float {
    return sqrt((offset1.x - offset2.x).pow(2) + (offset1.y - offset2.y).pow(2))
}


private fun isWithinCanvasBounds(offset: Offset, canvasSize: Size): Boolean {
    return offset.x >= 0f && offset.x <= canvasSize.width && offset.y >= 0f && offset.y <= canvasSize.height
}