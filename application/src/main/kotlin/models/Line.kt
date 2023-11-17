package models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.UUID

data class Line(
    val id: Int = 0,
    var startOffset: Offset,
    var endOffset: Offset,
    val color: Color = Color.Black,
    val strokeWidth: Dp = 1.dp
)
