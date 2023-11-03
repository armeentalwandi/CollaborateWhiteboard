package composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import models.toHSL
import kotlin.math.atan2

@Composable
fun ColourWheel(
    selectedColour: Color,
    onColourSelected: (Color) -> Unit
) {
    println(selectedColour)
    var saturation by remember { mutableStateOf(1f) }
    var lightness by remember { mutableStateOf(0.5f) }
    val size = 200.dp
    val strokeWidth = 20.dp
    val interactionSource = remember { MutableInteractionSource() }
    val colours = listOf(
        Color(255, 0, 0),       // Red
        Color(255, 83, 0),     // Red-Orange
        Color(255, 165, 0),    // Orange
        Color(255, 248, 0),    // Yellow-Orange
        Color(255, 255, 0),    // Yellow
        Color(127, 255, 0),    // Yellow-Green
        Color(0, 128, 0),      // Green
        Color(0, 127, 170),    // Blue-Green
        Color(0, 0, 255),      // Blue
        Color(127, 0, 255),    // Blue-Violet
        Color(148, 0, 211),    // Violet
        Color(227, 0, 140)     // Red-Violet
    )

    Column(
        modifier = Modifier.background(Color.White), // This ensures that the entire component has a gray background.
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .size(width = 150.dp, height = 50.dp)
                .background(Color.White)
        ) {
            // drawRect(color = adjustColorByHSL(selectedColour, saturation, lightness))
            drawRect(color=selectedColour)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .size(200.dp)
                .pointerInput(interactionSource) {
                    detectTapGestures { offset ->
                        val centerX = size / 2f
                        val centerY = size / 2f
                        val dx = offset.x - centerX.toPx()
                        val dy = offset.y - centerY.toPx()

                        val angle = (atan2(dy.toDouble(), dx.toDouble()) + 2 * Math.PI) % (2 * Math.PI)

                        val fraction = angle / (2 * Math.PI)
                        val colorIndex = (fraction * colours.size).toInt() % colours.size
                        val colourAtPosition = colours[colorIndex]

                        // Adjust the selected color using the saturation and lightness sliders
                        // val adjustedColor = adjustColorByHSL(colourAtPosition, saturation, lightness)

                        onColourSelected(colourAtPosition)
                    }
                }
                .background(Color.White)
        ) {
            drawIntoCanvas {
                val radius = ((size.toPx() - strokeWidth.toPx()) / 2)
                drawCircle(
                    brush = Brush.sweepGradient(colours),
                    center = center,
                    radius = radius,
                )
            }
        }

        Slider(
            value = saturation,
            onValueChange = {
                saturation = it;
            },
            valueRange = 0f..1f,
            steps = 100
        )
        Text("Saturation")

        Slider(
            value = lightness,
            onValueChange = {
                lightness = it;
            },
            valueRange = 0f..1f,
            steps = 100
        )
        Text("Lightness")
    }
}