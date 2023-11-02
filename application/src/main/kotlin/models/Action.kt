package models

import androidx.compose.ui.graphics.Color
sealed class Action {
    data class AddStroke(val stroke: Stroke) : Action()
    data class ChangeColor(val color: Color) : Action()
    data class DeleteStroke(val deletedStroke: Stroke) : Action()
}
