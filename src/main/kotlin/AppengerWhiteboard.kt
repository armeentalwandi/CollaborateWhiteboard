import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import composables.TwoColumnLayout

@Composable
@Preview
fun App() {
    TwoColumnLayout()
}

fun main() = application {
    Window(
        title = "Appengers Whiteboard",
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
