package composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import models.ShapeType

@Composable
fun ShapePicker(onShapeSelected: (ShapeType) -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        ShapeType.entries.forEach { shapeType ->
            Button(
                onClick = { onShapeSelected(shapeType) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = shapeType.name)
            }
        }
    }
}