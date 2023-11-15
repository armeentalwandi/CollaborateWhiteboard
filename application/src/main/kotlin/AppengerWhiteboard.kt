import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import composables.loginPage
import composables.twoColumnLayout
import composables.registrationPage

// Create an instance of the ApiClient class for making API requests
val apiClient: ApiClient = ApiClient()

// Temporary UUID for testing purposes
const val TEMP_UUID = "af7c1fe6-d669-414e-b066-e9733f0de7a8"

// Enum to represent different screens in the application
enum class Screen {
    Welcome, Login, Register, TwoColumnLayout
}

// Main Composable function for the application
@Composable
@Preview
fun app() {

    var currentScreen by remember { mutableStateOf(Screen.Welcome) }

    // Compose UI based on the current screen
    when (currentScreen) {
        Screen.Welcome -> welcomePage(
            onLoginClick = {currentScreen = Screen.Login},
            onRegisterClick = {currentScreen = Screen.Register})

        Screen.Login -> loginPage(
            onLoginSuccessful = { currentScreen = Screen.TwoColumnLayout },
            onBack = {currentScreen = Screen.Welcome},
            onNoAccount = { currentScreen = Screen.Register })

        Screen.Register -> registrationPage(
            onRegistrationSuccessful =  { currentScreen = Screen.Login },
            onBack = {currentScreen = Screen.Welcome})

        Screen.TwoColumnLayout -> twoColumnLayout()
    }
}

// Main function to start the application
fun main() = application {

    Window(
        title = "Appenger's whiteboard",
        onCloseRequest = ::exitApplication
    ) {
        // Call the main Composable function to build the UI
        app()
    }
}


