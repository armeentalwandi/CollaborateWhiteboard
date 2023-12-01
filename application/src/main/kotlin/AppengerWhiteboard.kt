
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import composables.*
import models.AppData
import models.UserPreferences
import java.awt.Dimension
import java.io.File

val ENVIRONMENT = "local"

// Create an instance of the ApiClient class for making API requests
val apiClient: ApiClient = ApiClient()
val directoryPath = "${System.getProperty("user.home")}/AppengersWhiteboard" // Replace with your desired directory path


// Enum to represent different screens in the application
enum class Screen {
    Welcome, Login, Register, RoomsDashboard, TwoColumnLayout
}

// Main Composable function for the application
@Composable
@Preview
fun app() {

    var currentScreen by remember { mutableStateOf(Screen.Welcome) }
    val appData by remember { mutableStateOf(AppData(null, null)) }

    Column {
        // Compose UI based on the current screen
        when (currentScreen) {
            Screen.Welcome -> welcomePage(
                onLoginClick = {currentScreen = Screen.Login},
                onRegisterClick = {currentScreen = Screen.Register})

            Screen.Login -> loginPage(
                onLoginSuccessful = { currentScreen = Screen.RoomsDashboard },
                onBack = {currentScreen = Screen.Welcome},
                onNoAccount = { currentScreen = Screen.Register },
                data = appData)

            Screen.Register -> registrationPage(
                onRegistrationSuccessful =  { currentScreen = Screen.Login },
                onBack = {currentScreen = Screen.Welcome})

            Screen.RoomsDashboard -> roomsDashboard(
                appData = appData,
                onSignOut = { currentScreen = Screen.Login },
                onGoToWhiteboard = { currentScreen = Screen.TwoColumnLayout}
            )

            Screen.TwoColumnLayout -> twoColumnLayout(
                data = appData,
                onBack = { currentScreen = Screen.RoomsDashboard }
            )
        }
    }
}

// Main function to start the application
fun main() = application {
    val directory = File(directoryPath)
    if (!directory.exists()) {
        val wasCreated = directory.mkdirs()
        if (wasCreated) {
            println("Directory created successfully.")
        } else {
            println("Failed to create directory.")
        }
    } else {
        println("Directory already exists.")
    }

    UserPreferences.loadPreferences()
    val windowState = rememberWindowState(width = UserPreferences.windowWidth, height = UserPreferences.windowHeight)

    Window(
        title = "Appenger's whiteboard",
        onCloseRequest = {
            UserPreferences.windowWidth = windowState.size.width
            UserPreferences.windowHeight = windowState.size.height
            UserPreferences.savePreferences()
            exitApplication()
        },
        resizable = true,
        state = windowState
    ) {
        window.minimumSize = Dimension(900,700)

        app()

        // Observe the window size changes
        LaunchedEffect(windowState.size) {
            UserPreferences.windowWidth = windowState.size.width
            UserPreferences.windowHeight = windowState.size.height
        }

    }
}


