package models

import models.Line
import androidx.compose.ui.graphics.Color
sealed class Action {
    data class AddLine(val line: Line) : Action()
    data class ChangeColor(val color: Color) : Action()
    // ... add other actions as needed
}
