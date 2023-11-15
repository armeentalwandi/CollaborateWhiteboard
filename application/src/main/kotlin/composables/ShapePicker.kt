package composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import models.ShapeType

// Composable function for shape options dialog buttons
@Composable
fun shapeOptionsDialogButtons(
    onShapeSelected: (ShapeType) -> Unit
) {
    // Column to layout shape option buttons vertically
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Display shape options in the dialog using shapeOptionButton
        shapeOptionButton(ShapeType.Rectangle, onShapeSelected)
        shapeOptionButton(ShapeType.Circle, onShapeSelected)
        shapeOptionButton(ShapeType.Triangle, onShapeSelected)
        // Add more shape options as needed
    }
}

// Composable function for a shape option button
@Composable
fun shapeOptionButton(
    shapeType: ShapeType,
    onClick: (ShapeType) -> Unit
) {
    // Button for selecting a shape type
    Button(
        onClick = { onClick(shapeType) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(shapeType.name) // You may need to define a name for each shape type
    }
}