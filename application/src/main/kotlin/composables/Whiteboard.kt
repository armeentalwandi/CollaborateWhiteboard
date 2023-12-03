package composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

import apiClient
import kotlinx.coroutines.*
import models.*

import java.util.UUID
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

import androidx.compose.ui.graphics.drawscope.Stroke as Stroke2

@Composable
fun whiteboard(selectedMode: String = "DRAW_LINES", shape: ShapeType? = null, appData: AppData, canEdit: Boolean) {

    // Variable declarations for managing state and storing whiteboard elements
    var strokeSize by remember { mutableStateOf(1f) } // Define slider value here
    var colour by remember { mutableStateOf(Color(255, 0, 0)) }

    val lines = remember { mutableStateListOf<Line>() }
    val strokes = remember { mutableStateListOf<Stroke>() }
    val coroutineScope = rememberCoroutineScope()
    val updateIntervalMillis = 2000L // 2 seconds

    var currentStroke: Stroke? by remember { mutableStateOf(null) }
    var canvasSize by remember { mutableStateOf(Size(0f, 0f)) }
    var colourPickerDialog: Boolean by remember { mutableStateOf(false) }
    val undoStack = remember { mutableStateListOf<Action>() }
    val redoStack = remember { mutableStateListOf<Action>() }

    var selectionStart by remember { mutableStateOf<Offset?>(null) }
    var selectionEnd by remember { mutableStateOf<Offset?>(null) }
    var isSelecting by remember { mutableStateOf(false) }

    var selectedStrokes = remember { mutableStateListOf<Stroke>() }
    var moveStart by remember { mutableStateOf<Offset?>(null) }

    var isMovingStroke by remember { mutableStateOf(false) }

    var currentShape by remember { mutableStateOf(shape) }

    val clipboardManager = LocalClipboardManager.current
    var showCustomSnackbar by remember { mutableStateOf(false) }

    // Retrieving strokes from the server on composition for a specific user

    LaunchedEffect(shape) {
        currentShape = shape
    }
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while(isActive){
                val newStrokes = mutableListOf<Stroke>()
                val newLines = mutableListOf<Line>()
                apiClient.getAllStrokes(UUID.fromString(appData.currRoom!!.roomId)).forEach {
                    val stroke = fromSerializable(it)
                    newStrokes.add(stroke)
                    newLines.addAll(stroke.lines)
                }

                withContext(Dispatchers.Main) {
                    strokes.clear()
                    strokes.addAll(newStrokes)
                    lines.clear()
                    lines.addAll(newLines)
                }
                delay(updateIntervalMillis)
            }
        }
    }

    // Function to handle undo action
    fun undo() {
        if (undoStack.isNotEmpty()) {
            val lastAction = undoStack.removeLast()
            when (lastAction) {
                is Action.AddStroke -> {
                    runBlocking {
                        launch {
                            apiClient.deleteStroke(UUID.fromString(lastAction.stroke.strokeId))
                        }
                    }
                    strokes.remove(lastAction.stroke)
                    lines.removeAll(lastAction.stroke.lines)
                }
                is Action.DeleteStroke -> {
                    strokes.add(lastAction.deletedStroke)
                    lines.addAll(lastAction.deletedStroke.lines)
                    runBlocking {
                        launch {
                            apiClient.postStroke(toSerializable(lastAction.deletedStroke))
                        }
                    }
                }
                is Action.ChangeColor -> { // Restore to the previous color or handle accordingly
                }
            }
            redoStack.add(lastAction)  // Push to redo stack
        }
    }

    // Function to handle redo action
    fun redo() {
        if (redoStack.isNotEmpty()) {
            val actionToRedo = redoStack.removeLast()
            when (actionToRedo) {
                is Action.AddStroke -> {
                    strokes.add(actionToRedo.stroke)
                    lines.addAll(actionToRedo.stroke.lines)
                    runBlocking {
                        launch {
                            apiClient.postStroke(toSerializable(actionToRedo.stroke))
                        }
                    }
                }
                is Action.DeleteStroke -> {
                    strokes.remove(actionToRedo.deletedStroke)
                    actionToRedo.deletedStroke.lines.forEach {
                        lines.remove(it)
                    }
                    runBlocking {
                        launch {
                            apiClient.deleteStroke(UUID.fromString(actionToRedo.deletedStroke.strokeId))
                        }
                    }
                }
                is Action.ChangeColor -> {
                    // Apply the color change again
                }
            }
            undoStack.add(actionToRedo)  // Push back to history
        }
    }


    // Row with undo, redo buttons, slider, and color picker button
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // Undo Button
        Button(onClick = { undo() }, enabled = undoStack.isNotEmpty()) {
            Image(
                painter = painterResource("undo.svg"),
                contentDescription = "Undo",
                modifier = Modifier.background(Color.Transparent)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))  // Add some space between the buttons

        // Redo Button
        Button(onClick = { redo() }, enabled = redoStack.isNotEmpty()) {
            Image(
                painter = painterResource("redo.svg"),
                contentDescription = "Redo",
                modifier = Modifier.background(Color.Transparent)
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Dynamic spacing to push the slider to the left

        // Slider for adjusting stroke size
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

        // Button to open the color picker dialog
        TextButton(
            onClick = {
                colourPickerDialog = true
            },
            modifier = Modifier.background(colour).weight(1f)
        ) {}

        Spacer(modifier = Modifier.weight(1f)) // Dynamic spacing to push the row to the right

        val authText = if (canEdit) { "Editing Access" } else { "Viewing Access"}

        TextButton(onClick = {}, enabled = false) {
            Text(text = "Privilege: $authText")
        }

        TextButton(onClick = {
            clipboardManager.setText(AnnotatedString(appData.currRoom!!.roomCode))
            showCustomSnackbar = true
        }) {
            Text(text = "Room Code: ${appData.currRoom!!.roomCode}")
        }

    }

    CustomSnackbar(
        message = "Room Code Copied to Clipboard",
        visible = showCustomSnackbar,
        onDismiss = { showCustomSnackbar = false }
    )

    Spacer(modifier = Modifier.height(16.dp)) // Add spacing between the row and the canvas
    var initialDragPosition: Offset? = null


    // Handling drag gestures for drawing and other interactions on the canvas
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                canvasSize = size.toSize()
            }
            .background(Color.White)
            .pointerInput(selectedMode) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Logic for handling drag start based on the selected mode
                        if (canEdit) {
                            initialDragPosition = offset
                            if (selectedMode == "DRAW_LINES") {
                                currentStroke = Stroke(
                                    offset,
                                    userId = appData.user!!.userId,
                                    strokeId = UUID.randomUUID().toString(),
                                    roomId = appData.currRoom!!.roomId,
                                    lines = mutableListOf()
                                )
                            } else if (selectedMode == "DRAW_SHAPES") {
                                if (currentShape == ShapeType.Circle) {
                                    currentStroke = createCircleStroke(
                                        center = offset,
                                        initialRadius = 0f,
                                        colour = colour,
                                        strokeSize = strokeSize,
                                        canvasSize = canvasSize,
                                        appData = appData
                                    )
                                } else if (currentShape == ShapeType.Rectangle) {
                                    currentStroke = createRectangleStroke(
                                        topLeft = offset,
                                        bottomRight = offset,
                                        colour = colour,
                                        strokeSize = strokeSize,
                                        appData = appData
                                    )
                                } else if (currentShape == ShapeType.Triangle) {
                                    currentStroke = createTriangleStroke(
                                        vertex1 = offset,
                                        dragEnd = Offset(offset.x + 1f, offset.y + 1f),
                                        colour = colour,
                                        strokeSize = strokeSize,
                                        canvasSize = canvasSize,
                                        appData = appData
                                    )
                                }
                            } else if (selectedMode == "SELECT_LINES") {
                                if (isMovingStroke) {
                                    moveStart = offset
                                } else {
                                    selectionStart = offset
                                    selectionEnd = offset
                                    isSelecting = true
                                }
                            }
                        }
                    },

                    onDrag = { change, dragAmount ->
                        // Logic for handling drag (e.g., drawing lines, erasing, drawing shapes)
                        if (canEdit) {
                            change.consume()
                            val startPosition = change.position - dragAmount
                            val endPosition = change.position
                            if (isWithinCanvasBounds(startPosition, canvasSize) && isWithinCanvasBounds(
                                    endPosition,
                                    canvasSize
                                )
                            ) {
                                if (selectedMode == "DRAW_LINES") {
                                    // Check if the line is within the canvas bounds
                                    val line = Line(
                                        id = if (lines.size > 0) {
                                            lines.last().id + 1
                                        } else {
                                            0
                                        },
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
                                    var erasableStroke: Stroke? = null
                                    for (stroke in strokes) {
                                        for (line in stroke.lines) {
                                            if (doLinesCross(
                                                    startPosition,
                                                    endPosition,
                                                    line.startOffset,
                                                    line.endOffset
                                                )
                                            ) {
                                                erasableStroke = stroke
                                                break
                                            }
                                        }
                                    }

                                    if (erasableStroke != null) {
                                        // Send deletion to the server
                                        runBlocking {
                                            launch {
                                                apiClient.deleteStroke(UUID.fromString(erasableStroke.strokeId))
                                            }
                                        }
                                        strokes.remove(erasableStroke)
                                        erasableStroke.lines.forEach {
                                            lines.remove(it)
                                        }
                                        // Add the deleted stroke to history for undo functionality
                                        undoStack.add(Action.DeleteStroke(erasableStroke))
                                        redoStack.clear()  // Clear redo stack when a new action is done
                                    }

                                } else if (selectedMode == "DRAW_SHAPES") {
                                    if (currentShape == ShapeType.Circle) {
                                        val radius = distanceBetweenTwoPoints(currentStroke!!.center!!, endPosition)
                                        currentStroke = createCircleStroke(
                                            currentStroke!!.center!!,
                                            radius,
                                            colour,
                                            strokeSize,
                                            canvasSize,
                                            appData = appData
                                        )
                                    } else if (currentShape == ShapeType.Rectangle) {
                                        currentStroke = createRectangleStroke(
                                            topLeft = currentStroke!!.startOffset,
                                            bottomRight = endPosition,
                                            colour = colour,
                                            strokeSize = strokeSize,
                                            appData = appData
                                        )
                                    } else if (currentShape == ShapeType.Triangle) {
                                        currentStroke = createTriangleStroke(
                                            vertex1 = currentStroke!!.startOffset,
                                            dragEnd = endPosition,
                                            colour = colour,
                                            strokeSize = strokeSize,
                                            canvasSize = canvasSize,
                                            appData = appData
                                        )
                                    }
                                } else if (selectedMode == "SELECT_LINES") {
                                    if (isMovingStroke) {
                                        val currentMoveStart = moveStart
                                        if (currentMoveStart != null) {
                                            selectedStrokes.forEach { stroke ->
                                                val canMove = stroke.lines.all { line ->
                                                    isLineWithinCanvasBounds(line, dragAmount, canvasSize)
                                                }
                                                if (canMove) {
                                                    stroke.lines.forEach { line ->
                                                        line.startOffset += dragAmount
                                                        line.endOffset += dragAmount
                                                    }
                                                    stroke.startOffset += dragAmount
                                                    stroke.endOffset += dragAmount
                                                }
                                            }
                                            moveStart = change.position
                                        }
                                    } else {
                                        // Handle selection box update
                                        selectionEnd = change.position
                                    }
                                }
                            }
                        }
                    },
                    onDragEnd = {
                        // Logic for handling drag end (e.g., completing strokes)
                        if (canEdit) {
                            if (selectedMode == "DRAW_LINES") {
                                currentStroke?.endOffset = currentStroke?.lines?.last()?.endOffset!!
                                strokes.add(currentStroke!!)
                                undoStack.add(Action.AddStroke(currentStroke!!))
                                redoStack.clear()  // Clear redo stack when a new action is done
                            } else if (selectedMode == "DRAW_SHAPES") {
                                strokes.add(currentStroke!!)
                                lines.addAll(currentStroke!!.lines)
                                undoStack.add(Action.AddStroke(currentStroke!!))
                                redoStack.clear()  // Clear redo stack when a new action is done
                            } else if (selectedMode == "SELECT_LINES") {
                                if (isMovingStroke) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        updateStrokesInDatabase(selectedStrokes)
                                        isMovingStroke = false
                                        selectedStrokes.clear()
                                        moveStart = null
                                    }
                                } else if (selectionStart != null && selectionEnd != null) {
                                    selectedStrokes.clear()
                                    strokes.forEach { stroke ->
                                        if (isStrokeInSelectionBox(stroke, selectionStart!!, selectionEnd!!)) {
                                            selectedStrokes.add(stroke)  // Add stroke to selectedStrokes if it's within the selection box
                                        }
                                    }

                                    // Reset the selection box
                                    selectionStart = null
                                    selectionEnd = null
                                    isSelecting = false
                                    isMovingStroke = selectedStrokes.isNotEmpty()
                                }
                            }

                            runBlocking {
                                launch {
                                    if (currentStroke != null) {
                                        apiClient.postStroke(toSerializable(currentStroke!!))
                                    }
                                }
                            }
                            currentStroke = null
                        }
                    }
                )
            },
    ) {
        // Drawing current strokes and lines
        currentStroke?.lines?.forEach { currStrokeLine ->
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

        if (isSelecting) {
            selectionStart?.let { start ->
                selectionEnd?.let { end ->
                    if (selectionStart != null && selectionEnd != null) {
                        // Calculate the top-left corner of the selection box
                        val topLeft = Offset(
                            x = minOf(selectionStart!!.x, selectionEnd!!.x),
                            y = minOf(selectionStart!!.y, selectionEnd!!.y)
                        )

                        // Calculate the size of the selection box, ensuring width and height are positive
                        val size = Size(
                            width = abs(selectionEnd!!.x - selectionStart!!.x),
                            height = abs(selectionEnd!!.y - selectionStart!!.y)
                        )

                        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        val strokeStyle = Stroke2(width = 2f, pathEffect = dashEffect)

                        // Draw the selection box with the calculated top-left corner and size
                        drawRect(
                            color = Color.Black,
                            topLeft = topLeft,
                            size = size,
                            style = strokeStyle
                        )
                    }
                }
            }
        } else if (isMovingStroke) {
            selectedStrokes.forEach { stroke ->
                stroke.lines.forEach { line ->
                    drawLine(
                        color = Color.Yellow, // Highlight color
                        start = line.startOffset,
                        end = line.endOffset,
                        strokeWidth = line.strokeWidth.toPx() + 4.dp.toPx(), // Make the highlight line slightly thicker
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }

    // Dialog for choosing color
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
                colourWheel(colour, onColourSelected = { selectedColour ->
                    colour = selectedColour
                })
            }
        }
    }
}

// function to determine if 2 lines intersect, returns a boolean
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


private fun distanceBetweenTwoPoints(offset1: Offset, offset2: Offset): Float {
    return sqrt((offset1.x - offset2.x).pow(2) + (offset1.y - offset2.y).pow(2))
}


private fun isWithinCanvasBounds(offset: Offset, canvasSize: Size): Boolean {
    return offset.x >= 0f && offset.x <= canvasSize.width && offset.y >= 0f && offset.y <= canvasSize.height
}

fun isStrokeInSelectionBox(stroke: Stroke, selectionStart: Offset, selectionEnd: Offset): Boolean {
    val left = minOf(selectionStart.x, selectionEnd.x)
    val top = minOf(selectionStart.y, selectionEnd.y)
    val right = maxOf(selectionStart.x, selectionEnd.x)
    val bottom = maxOf(selectionStart.y, selectionEnd.y)

    // Check if any of the stroke's points are within the selection box
    return stroke.lines.any { line ->
        line.startOffset.x in left..right &&
                line.startOffset.y in top..bottom ||
                line.endOffset.x in left..right &&
                line.endOffset.y in top..bottom
    }
}

suspend fun updateStrokesInDatabase(movedStrokes: List<Stroke>) {
    val serializedStrokes = movedStrokes.map { stroke -> toSerializable(stroke) }
    apiClient.updateStrokes(serializedStrokes)
}
fun isLineWithinCanvasBounds(line: Line, dragAmount: Offset, canvasSize: Size): Boolean {
    val newStart = line.startOffset + dragAmount
    val newEnd = line.endOffset + dragAmount
    return newStart.x >= 0f && newStart.x <= canvasSize.width &&
            newStart.y >= 0f && newStart.y <= canvasSize.height &&
            newEnd.x >= 0f && newEnd.x <= canvasSize.width &&
            newEnd.y >= 0f && newEnd.y <= canvasSize.height
}

@Composable
fun CustomSnackbar(message: String, visible: Boolean, onDismiss: () -> Unit) {
    if (visible) {
        LaunchedEffect(Unit) {
            delay(800)  // Custom duration in milliseconds
            onDismiss()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = message,
                style = TextStyle(fontSize = 13.sp),
                color = Color.DarkGray,
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(8.dp)
            )
        }
    }
}

