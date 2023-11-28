import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.awt.Desktop
import java.net.URI

@Composable
fun helpButton() {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null

    TextButton(
        onClick = {
            desktop?.let {
                if (it.isSupported(Desktop.Action.BROWSE)) {
                    it.browse(URI("https://appengers.netlify.app/help"))
                }
            }
        },
        modifier = Modifier
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource("help.svg"),
            contentDescription = "Help",
            modifier = Modifier.background(Color.Transparent)
        )
    }
}