package composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import models.ShapeType

@Composable
fun ShapeOptionsDialogButtons(
    onShapeSelected: (ShapeType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Display shape options in the dialog
        ShapeOptionButton(ShapeType.Rectangle, onShapeSelected)
        ShapeOptionButton(ShapeType.Circle, onShapeSelected)
        ShapeOptionButton(ShapeType.Triangle, onShapeSelected)
        // Add more shape options as needed
    }
}

@Composable
fun ShapeOptionButton(
    shapeType: ShapeType,
    onClick: (ShapeType) -> Unit
) {
    Button(
        onClick = { onClick(shapeType) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(shapeType.name) // You may need to define a name for each shape type
    }
}