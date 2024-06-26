package composables

import PromptDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import apiClient
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import directoryPath
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.AppData
import models.ShapeType
import models.Stroke
import models.fromSerializable
import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.net.URI
import java.util.*
import kotlin.math.max
import kotlin.math.min

// Enumeration to represent different drawing modes
enum class Mode(val resource: String) {
    DRAW_LINES("pen.svg"),
    ERASE("eraser.svg"),
    SELECT_LINES("select.svg"),
    DRAW_SHAPES("shapes.svg")
}

@Composable
fun twoColumnLayout(data: AppData, onBack: () -> Unit) {
    // Left Column
    var selectedMode by remember { mutableStateOf(Mode.DRAW_LINES) }
    var selectedShape by remember { mutableStateOf(ShapeType.Circle) }
    val showShapesDropdownMenu = remember { mutableStateOf(false) }
    val dropdownMenuPosition = remember { mutableStateOf(Offset(0f, 0f)) }
    val canEdit = (data.currRoom!!.isCourse && data.user!!.auth_level == "professor") || (!data.currRoom!!.isCourse)


    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Back button
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource("back.svg"),
                            contentDescription = "Back",
                            modifier = Modifier.background(Color.Transparent)
                        )
                    }

                    Mode.entries.forEach { mode ->
                        if (mode == Mode.DRAW_SHAPES) {
                            // Shapes button with dropdown logic
                            modeButton(Mode.DRAW_SHAPES, enabled = canEdit, onClick = { showShapesDropdownMenu.value = true })
                            // DropdownMenu for shapes
                            DropdownMenu(
                                expanded = showShapesDropdownMenu.value,
                                onDismissRequest = { showShapesDropdownMenu.value = false },
                                modifier = Modifier.absoluteOffset(
                                    x = dropdownMenuPosition.value.x.dp,
                                    y = dropdownMenuPosition.value.y.dp
                                )
                            ) {
                                DropdownMenuItem(onClick = {
                                    selectedMode = Mode.DRAW_SHAPES
                                    selectedShape = ShapeType.Rectangle
                                    showShapesDropdownMenu.value = false
                                }) {
                                    Text("Rectangle")
                                }
                                DropdownMenuItem(onClick = {
                                    selectedMode = Mode.DRAW_SHAPES
                                    selectedShape = ShapeType.Circle
                                    showShapesDropdownMenu.value = false
                                }) {
                                    Text("Circle")
                                }
                                DropdownMenuItem(onClick = {
                                    selectedMode = Mode.DRAW_SHAPES
                                    selectedShape = ShapeType.Triangle
                                    showShapesDropdownMenu.value = false
                                }) {
                                    Text("Triangle")
                                }
                            }
                        } else {
                            modeButton(mode, {
                                selectedMode = mode
                            }, canEdit)
                        }
                    }

                    exportButton(data)

                    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null

                    IconButton(
                        onClick = {
                            desktop?.let {
                                if (it.isSupported(Desktop.Action.BROWSE)) {
                                    it.browse(URI("https://appengers.netlify.app/help"))
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(19.dp)
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = "Info", modifier=Modifier.size(25.dp))
                    }
                }

                // Right Column
                Column(
                    modifier = Modifier
                        .weight(4f)
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    whiteboard(appData = data, selectedMode = selectedMode.name, shape = selectedShape, canEdit = canEdit)
                }
            }
        }
    }
}

// Composable function for a button representing a drawing mode
@Composable
fun modeButton(mode: Mode, onClick: () -> Unit, enabled: Boolean) {
    // TextButton with an Image representing the drawing mode
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp),
        enabled = enabled
    ) {
        Image(
            painter = painterResource(mode.resource),
            contentDescription = null,
            modifier = Modifier.background(Color.Transparent)
        )
    }
}

@Composable
fun exportButton(appData: AppData) {
    var showExportStatusDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    var savePath = "$directoryPath/${appData.user!!.first_name}_${appData.user!!.last_name}_${appData.currRoom!!.roomName}.pdf"

    TextButton(
        onClick = {
            // Export Logic
            val selectedPath = showSaveDialog()
            if (selectedPath != null) {
                savePath = selectedPath
                if (!savePath.endsWith(".pdf")) {
                    savePath += ".pdf"
                }
                runBlocking {
                    launch {
                        // Retrieving strokes from the server on composition for a specific user
                        val strokes = mutableListOf<Stroke>()
                        var minX = 1000000f
                        var maxX = 0f
                        var minY = 1000000f
                        var maxY = 0f
                        apiClient.getAllStrokes(UUID.fromString(appData.currRoom!!.roomId)).forEach {
                            val stroke = fromSerializable(it)
                            strokes.add(stroke)

                            stroke.lines.forEach {line ->
                                val miX = min(line.startOffset.x, line.endOffset.x)
                                val maX = max(line.startOffset.x, line.endOffset.x)
                                val miY = min(line.startOffset.y, line.endOffset.y)
                                val maY = max(line.startOffset.y, line.endOffset.y)

                                if (miX <= minX) {
                                    minX = miX
                                }
                                if (miY <= minY) {
                                    minY = miY
                                }
                                if (maX >= maxX) {
                                    maxX = maX
                                }
                                if (maY >= maxY) {
                                    maxY = maY
                                }
                            }
                        }

                        println("($minX, $minY) ($maxX, $maxY)")

                        val pageWidth = (maxX - minX) + 100 // Adding some margin
                        val pageHeight = (maxY - minY) + 100 // Adding some margin

                        try {
                            val writer = PdfWriter(savePath)
                            val pdfDoc = PdfDocument(writer)
                            val page = pdfDoc.addNewPage(PageSize(pageWidth, pageHeight))
                            val canvas = PdfCanvas(page)

                            for (stroke in strokes) {
                                for (line in stroke.lines) {
                                    val color = line.color
                                    val rgbColor = DeviceRgb(
                                        color.red,
                                        color.green,
                                        color.blue
                                    )
                                    val strokeWidth = line.strokeWidth.value

                                    canvas.setStrokeColor(rgbColor)
                                    canvas.setLineWidth(strokeWidth)

                                    // Drawing the line
                                    val startX = line.startOffset.x - minX + 50
                                    val startY = pageHeight - (line.startOffset.y - minY + 50)
                                    val endX = line.endOffset.x - minX + 50
                                    val endY = pageHeight - (line.endOffset.y - minY + 50)

                                    canvas.moveTo(startX.toDouble(), startY.toDouble())
                                    canvas.lineTo(endX.toDouble(), endY.toDouble())
                                    canvas.stroke()
                                }
                            }

                            page.flush()
                            pdfDoc.close()
                            writer.close()

                            dialogMessage = "Document exported as PDF to '$savePath' successfully!"
                            showExportStatusDialog = true
                        } catch (e: Exception) {
                            dialogMessage = "Failed to export document: ${e.localizedMessage}"
                            showExportStatusDialog = true
                        }
                    }
                }
            }
        },
        modifier = Modifier
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource("save.svg"),
            contentDescription = "Export",
            modifier = Modifier.background(Color.Transparent)
        )

        if (showExportStatusDialog) {
            PromptDialog(onDismissRequest = {showExportStatusDialog = false}, "Export Status", dialogMessage, "OK")
        }
    }

}
fun showSaveDialog(): String? {
    val frame = Frame().apply { isVisible = true }
    val fileDialog = FileDialog(frame, "Save As", FileDialog.SAVE).apply {
        // Set initial directory, file name, etc. if needed
        isVisible = true
    }

    val directory = fileDialog.directory
    val file = fileDialog.file
    frame.dispose() // Close and dispose of the frame

    return if (file != null) "$directory$file" else null
}
