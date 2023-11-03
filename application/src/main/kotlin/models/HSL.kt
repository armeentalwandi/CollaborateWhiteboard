package models

import androidx.compose.ui.graphics.Color


data class HSL(val hue: Float, val saturation: Float, val lightness: Float)

fun Color.toHSL(): HSL {
    val r = red
    val g = green
    val b = blue

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)

    val delta = max - min

    val lightness = (max + min) / 2

    val saturation = if (max == min) {
        0f
    } else {
        val deltaL = if (lightness < 0.5) delta / (max + min) else delta / (2.0f - max - min)
        deltaL
    }

    val hue = when {
        r == max -> ((g - b) / delta + (if (g < b) 6 else 0)) / 6f
        g == max -> ((b - r) / delta + 2) / 6f
        b == max -> ((r - g) / delta + 4) / 6f
        else -> 0f
    }

    return HSL(hue, saturation, lightness)
}

fun HSL.toColor(): Color {
    val q = if (lightness < 0.5) lightness * (1 + saturation) else lightness + saturation - lightness * saturation
    val p = 2 * lightness - q
    return Color(
        hueToRgb(p, q, hue + 1f / 3),
        hueToRgb(p, q, hue),
        hueToRgb(p, q, hue - 1f / 3)
    )
}

private fun hueToRgb(p: Float, q: Float, t: Float): Float {
    var tempT = t
    if (tempT < 0) tempT += 1
    if (tempT > 1) tempT -= 1
    if (tempT < 1f / 6) return p + (q - p) * 6f * tempT
    if (tempT < 1f / 2) return q
    return if (tempT < 2f / 3) p + (q - p) * (2f / 3 - tempT) * 6 else p
}

fun adjustColorByHSL(color: Color, saturation: Float, lightness: Float): Color {
    val hsl = color.toHSL()

    // Adjust the saturation and lightness
    val newSaturation = (hsl.saturation * saturation).coerceIn(0f, 1f)
    val newLightness = (hsl.lightness * lightness).coerceIn(0f, 1f)

    // Convert back to RGB
    return HSL(hsl.hue, newSaturation, newLightness).toColor()
}
