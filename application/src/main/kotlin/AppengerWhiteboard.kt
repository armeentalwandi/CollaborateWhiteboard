import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import composables.LoginPage
import composables.TwoColumnLayout
import composables.RegistrationPage


val apiClient: ApiClient = ApiClient()
val TEMP_UUID = "af7c1fe6-d669-414e-b066-e9733f0de7a8"

@Composable
@Preview
fun App() {
    //RegistrationPage()
    //LoginPage()
    TwoColumnLayout()
}

fun main() = application {
    Window(
        title = "Appenger's Whiteboard",
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
