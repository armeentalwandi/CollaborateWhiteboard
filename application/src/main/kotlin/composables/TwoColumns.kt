package composables

import PromptDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import apiClient
import helpButton
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.AppData
import models.Stroke
import models.fromSerializable
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import java.awt.FileDialog
import java.awt.Frame
import java.io.IOException
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

// Composable function for the two-column layout for the WhiteBoard
@Composable
fun twoColumnLayout(data: AppData, onBack: () -> Unit) {
    // Left Column
    var selectedMode by remember { mutableStateOf(Mode.DRAW_LINES) }

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
                        modeButton(mode) {
                            selectedMode = mode
                        }
                    }

                    helpButton()
                    exportButton(data)
                }

                // Right Column
                Column(
                    modifier = Modifier
                        .weight(4f)
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // whiteboard component with the selected drawing mode
                    whiteboard(appData = data, selectedMode = selectedMode.name, shape = null)
                }
            }
        }
    }
}

// Composable function for a button representing a drawing mode
@Composable
fun modeButton(mode: Mode, onClick: () -> Unit) {
    // TextButton with an Image representing the drawing mode
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
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

    var savePath = "${System.getProperty("user.home")}/AppengersWhiteboard/${appData.user!!.first_name}_${appData.user!!.last_name}_${appData.currRoom!!.roomName}.pdf"

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

                        val pageHeight = (maxY - minY) + 100
                        val pageWidth = (maxX - minX) + 100
                        // Create a new PDF document
                        val document = PDDocument()
                        val page = PDPage(PDRectangle(0f,0f,pageWidth, pageHeight))
                        document.addPage(page)

                        try {
                            val contentStream = PDPageContentStream(document, page)

                            for (stroke in strokes) {
                                for (line in stroke.lines) {
                                    val color = line.color
                                    // Flip the y-coordinate
                                    val startY = pageHeight - (line.startOffset.y - minY)
                                    val endY = pageHeight - (line.endOffset.y - minY)
                                    val strokeWidth = line.strokeWidth.value // Assuming strokeWidth is in points

                                    val awtColor = java.awt.Color(
                                        (color.red * 255).toInt(),
                                        (color.green * 255).toInt(),
                                        (color.blue * 255).toInt()
                                    )

                                    // Set color and stroke width
                                    contentStream.setStrokingColor(awtColor)
                                    contentStream.setLineWidth(strokeWidth)

                                    // Draw the line with transformed y-coordinate
                                    contentStream.moveTo(line.startOffset.x - minX + 25, startY - 25)
                                    contentStream.lineTo(line.endOffset.x - minX + 25, endY - 25)
                                    contentStream.stroke()
                                }
                            }

                            contentStream.close()
                            document.save(savePath)
                            dialogMessage = "Document exported as PDF to '$savePath' successfully!"
                            showExportStatusDialog = true
                            println("Document Exported as PDF to '$savePath' successfully!")
                        } catch (e: IOException) {
                            dialogMessage = "Export Failed :("
                            showExportStatusDialog = true
                            println("Export Failed :(")
                            e.printStackTrace()
                        } finally {
                            document.close()
                        }
                    }
                }
            }
        },
        modifier = Modifier
            .padding(16.dp)
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
