package models

import androidx.compose.animation.core.StartOffset
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.*

data class Stroke(
    var startOffset: Offset = Offset(0f, 0f),
    var endOffset: Offset = Offset(0f, 0f),
    var userId: UUID,
    var color: Color = Color.Black,
    val lines: MutableList<Line>
)