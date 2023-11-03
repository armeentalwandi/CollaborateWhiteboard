import androidx.compose.ui.graphics.Color

fun adjustColorByRGB(color: Color, lightness: Float): Color {
    return when {
        lightness < 0.5f -> Color(
            red = lerp(0f, color.red, lightness * 2),
            green = lerp(0f, color.green, lightness * 2),
            blue = lerp(0f, color.blue, lightness * 2)
        )
        else -> Color(
            red = lerp(color.red, 1f, (lightness - 0.5f) * 2),
            green = lerp(color.green, 1f, (lightness - 0.5f) * 2),
            blue = lerp(color.blue, 1f, (lightness - 0.5f) * 2)
        )
    }
}

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return (1 - fraction) * start + fraction * stop
}