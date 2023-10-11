import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import composables.TwoColumnLayout
import java.awt.Dimension

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
