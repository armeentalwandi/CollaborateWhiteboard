import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.ScrollableTabRow
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import composables.LoginPage
import composables.TwoColumnLayout
import composables.RegistrationPage


val apiClient: ApiClient = ApiClient()
val TEMP_UUID = "af7c1fe6-d669-414e-b066-e9733f0de7a8"

enum class Screen {
    Welcome, Login, Register, TwoColumnLayout
}

@Composable
@Preview
fun App() {

    var currentScreen by remember { mutableStateOf(Screen.Welcome) }

    when (currentScreen) {
        Screen.Welcome -> WelcomePage(onLoginClick = {currentScreen = Screen.Login}, onRegisterClick = {currentScreen = Screen.Register})
        Screen.Login -> LoginPage(onLoginSuccessful = { currentScreen = Screen.TwoColumnLayout }, onBack = {currentScreen = Screen.Welcome}, onNoAccount = { currentScreen = Screen.Register })
        Screen.Register -> RegistrationPage( onRegistrationSuccessful =  { currentScreen = Screen.Login }, onBack = {currentScreen = Screen.Welcome})
        Screen.TwoColumnLayout -> TwoColumnLayout()
    }
}

fun main() = application {

    Window(
        title = "Appenger's Whiteboard",
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}


