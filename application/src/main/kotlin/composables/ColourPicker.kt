import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round

@Composable
fun ColorPicker(
    initialColor: Color,
    onColorSelected: (Color) -> Unit
) {
    var red by remember { mutableStateOf(initialColor.red) }
    var green by remember { mutableStateOf(initialColor.green) }
    var blue by remember { mutableStateOf(initialColor.blue)}

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        ColorPreview(
            color = Color(red, green, blue),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        SliderValue(value = red, onValueChange = { red = it }, label = "Red")
        SliderValue(value = green, onValueChange = { green = it }, label = "Green")
        SliderValue(value = blue, onValueChange = { blue = it }, label = "Blue")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onColorSelected(Color(red, green, blue)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Select Color")
        }
    }
}

@Composable
fun ColorPreview(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.then(Modifier.background(color)),
    )
}

@Composable
fun SliderValue(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "$label: ${round(value * 255).toInt()}", fontSize = 16.sp)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            steps = 255,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
