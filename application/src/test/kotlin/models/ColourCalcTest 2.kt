package models


import adjustColorByRGB
import org.junit.jupiter.api.Test
import kotlin.math.abs
import androidx.compose.ui.graphics.Color
import lerp
import org.junit.jupiter.api.Assertions.*

class ColourCalcTest {

    private fun assertColorEquals(expected: Color, actual: Color, epsilon: Float = 0.01f) {
        assertTrue(abs(expected.red - actual.red) < epsilon)
        assertTrue(abs(expected.green - actual.green) < epsilon)
        assertTrue(abs(expected.blue - actual.blue) < epsilon)
    }

    @Test
    fun adjustColorByRGB_Darken() {
        val originalColor = Color(0.6f, 0.8f, 1.0f)
        val lightness = 0.3f // Should darken the color

        val expectedColor = Color(
            red = lerp(0f, originalColor.red, lightness * 2),
            green = lerp(0f, originalColor.green, lightness * 2),
            blue = lerp(0f, originalColor.blue, lightness * 2)
        )

        val actualColor = adjustColorByRGB(originalColor, lightness)
        assertColorEquals(expectedColor, actualColor)
    }

    @Test
    fun adjustColorByRGB_Lighten() {
        val originalColor = Color(0.2f, 0.4f, 0.6f)
        val lightness = 0.7f // Should lighten the color

        val expectedColor = Color(
            red = lerp(originalColor.red, 1f, (lightness - 0.5f) * 2),
            green = lerp(originalColor.green, 1f, (lightness - 0.5f) * 2),
            blue = lerp(originalColor.blue, 1f, (lightness - 0.5f) * 2)
        )

        val actualColor = adjustColorByRGB(originalColor, lightness)
        assertColorEquals(expectedColor, actualColor)
    }

    @Test
    fun lerp_Values() {
        assertEquals(0.5f, lerp(0f, 1f, 0.5f))
        assertEquals(1f, lerp(1f, 3f, 0f))
        assertEquals(2f, lerp(1f, 3f, 0.5f))
        assertEquals(3f, lerp(1f, 3f, 1f))
    }
}