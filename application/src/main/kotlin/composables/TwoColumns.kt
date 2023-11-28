package composables

import PromptDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
import java.awt.Desktop
import java.io.IOException
import java.net.URI
import java.util.*
import kotlin.math.exp
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
            runBlocking {
                launch {
                    // Retrieving strokes from the server on composition for a specific user
                    val strokes = mutableListOf<Stroke>()
                    var minX = 10000f
                    var maxX = 0f
                    var minY = 10000f
                    var maxY = 0f
                    apiClient.getAllStrokes(UUID.fromString(appData.currRoom!!.roomId)).forEach {
                        val stroke = fromSerializable(it)
                        strokes.add(stroke)

                        val miX = min(stroke.startOffset.x, stroke.endOffset.x)
                        val maX = max(stroke.startOffset.x, stroke.endOffset.x)
                        val miY = min(stroke.startOffset.y, stroke.endOffset.y)
                        val maY = max(stroke.startOffset.y, stroke.endOffset.y)

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

                    val pageHeight = (maxY - minY)
                    val pageWidth = (maxX - minX)
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
                                contentStream.moveTo(line.startOffset.x - minX, startY)
                                contentStream.lineTo(line.endOffset.x - minX, endY)
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
