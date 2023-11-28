package models

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class PreferencesData(
    val windowHeight: Float,
    val windowWidth: Float
)

object UserPreferences {
    var windowHeight: Dp = 720.dp
    var windowWidth: Dp = 1280.dp

    private val preferencesFile = File("${System.getProperty("user.home")}/appengers_user_preferences.json")

    fun loadPreferences() {
        if (preferencesFile.exists()) {
            val jsonString = preferencesFile.readText()
            val data = Json.decodeFromString<PreferencesData>(jsonString)
            windowHeight = data.windowHeight.dp
            windowWidth = data.windowWidth.dp
        }
    }

    fun savePreferences() {
        val data = PreferencesData(windowHeight.value, windowWidth.value)
        val jsonString = Json.encodeToString(data)
        preferencesFile.writeText(jsonString)
    }

    override fun toString(): String {
        return "Height: ${windowHeight}, Width: ${windowWidth}"
    }
}