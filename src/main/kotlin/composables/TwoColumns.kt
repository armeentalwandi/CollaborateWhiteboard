package composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp

@Composable
fun TwoColumnLayout() {
    MaterialTheme {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Left Column
            var drawButtonState by remember { mutableStateOf(true) }
            var eraseButtonState by remember { mutableStateOf(false) }
            var selectButtonState by remember { mutableStateOf(false) }
            var drawShapeButtonState by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(
                    onClick = {
                        drawButtonState = true
                        eraseButtonState = false
                        selectButtonState = false
                        drawShapeButtonState = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(if (drawButtonState) Color.LightGray else Color.Transparent)
                ) {
                    Image(
                        painter = painterResource("pen.svg"),
                        contentDescription = null,
                        modifier = Modifier.background(Color.Transparent)
                    )
                }

                TextButton(
                    onClick = {
                        drawButtonState = false
                        eraseButtonState = true
                        selectButtonState = false
                        drawShapeButtonState = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(if (eraseButtonState) Color.LightGray else Color.Transparent)
                ) {
                    Image(
                        painter = painterResource("eraser.svg"),
                        contentDescription = null,
                        modifier = Modifier.background(Color.Transparent)
                    )
                }

                TextButton(
                    onClick = {
                        drawButtonState = false
                        eraseButtonState = false
                        selectButtonState = true
                        drawShapeButtonState = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(if (selectButtonState) Color.LightGray else Color.Transparent)
                ) {
                    Image(
                        painter = painterResource("select.svg"),
                        contentDescription = null,
                        modifier = Modifier.background(Color.Transparent)
                    )
                }

                TextButton(
                    onClick = {
                        drawButtonState = false
                        eraseButtonState = false
                        selectButtonState = false
                        drawShapeButtonState = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(if (drawShapeButtonState) Color.LightGray else Color.Transparent)
                ) {
                    Image(
                        painter = painterResource("shapes.svg"),
                        contentDescription = null,
                        modifier = Modifier.background(Color.Transparent)
                    )
                }
            }

            // Right Column
            Column(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                // Canvas or right column content here
                Whiteboard()

            }
        }
    }
}