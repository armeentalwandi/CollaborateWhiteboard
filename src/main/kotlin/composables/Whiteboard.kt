package composables

import ColorPicker
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

import models.Line
import models.ShapeType

@Composable
fun Whiteboard(selectedMode: String = "DRAW_LINES", color: Color = Color.Black, shape: Shape? = null) {
    var strokeSize by remember { mutableStateOf(1f) } // Define slider value here
    var colour by remember { mutableStateOf(Color.Red) }
    val lines = remember { mutableStateListOf<Line>() }
    var canvasSize by remember { mutableStateOf(Size(0f, 0f)) }
    var colourPickerDialog: Boolean by remember { mutableStateOf(false) }

    var selectedShapeType by remember { mutableStateOf<ShapeType?>(null)  }
    var shapeSelectionDialog by remember { mutableStateOf(false) }


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

        if(!colourPickerDialog && selectedMode == "DRAW_LINES") {
            TextButton(
                onClick = {
                    println("Colour Button Clicked")
                    colourPickerDialog = true
                },
                modifier = Modifier.background(colour).weight(1f)
            ) {}
        }

        if (!shapeSelectionDialog && selectedMode == "DRAW_SHAPES") {
            TextButton(
                onClick = {
                    println("Shape Button Clicked")
                    shapeSelectionDialog = true
                },
                modifier = Modifier.background(colour).weight(1f)
            ) {}
        }
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
                                            color = colour,
                                            startOffset = startPosition,
                                            endOffset = endPosition,
                                            strokeWidth = strokeSize.toDp()
                                        )
                                        lines.add(line)
                                    }
                                } else if (selectedMode == "ERASE") {
                                    // ERASE LOGIC
                                } else if (selectedMode == "DRAW_SHAPES") {
                                    shapeSelectionDialog = true
                                } else if (selectedMode == "SELECT_LINES") {
                                    // SELECT ANYTHING WITHIN THE BOUNDS OF THE SELECTION
                                }
                            }
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

        when (selectedShapeType) {
            ShapeType.Rectangle -> {
                drawRect(
                    color = colour,
                    topLeft = Offset(100f, 100f), // Example position
                    size = Size(100f, 100f) // Example size
                )
            }
            ShapeType.Circle -> {
                drawCircle(
                    color = colour,
                    center = Offset(200f, 200f), // Example position
                    radius = 50f // Example radius
                )
            }
            ShapeType.Triangle -> {
                // Drawing triangle logic (you can use drawPath for this)
            }
            null -> Unit
        }
    }

    if (shapeSelectionDialog) {
        Dialog(
            onDismissRequest = {
                shapeSelectionDialog = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            ShapePicker(onShapeSelected = { selectedShape ->
                selectedShapeType = selectedShape
                shapeSelectionDialog = false
            })
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

private fun isWithinCanvasBounds(offset: Offset, canvasSize: Size): Boolean {
    return offset.x >= 0f && offset.x <= canvasSize.width && offset.y >= 0f && offset.y <= canvasSize.height
}