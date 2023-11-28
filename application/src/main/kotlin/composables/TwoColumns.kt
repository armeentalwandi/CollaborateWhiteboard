package composables

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
import helpButton
import models.AppData
import java.awt.Desktop
import java.net.URI

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
