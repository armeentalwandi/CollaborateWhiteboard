import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.Modifier
import java.awt.Desktop
import java.net.URI

// Composable function for the welcome page
@Composable
fun welcomePage(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome message
            Text(text = "Welcome to the Appenger's whiteboard!", style = MaterialTheme.typography.h4)

            // Login Button
            Button(onClick = onLoginClick) {
                Text(text = "Login")
            }

            // Register Button
            Button(onClick = onRegisterClick) {
                Text(text = "Register")
            }


            val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
            IconButton(
                onClick = {
                    desktop?.let {
                        if (it.isSupported(Desktop.Action.BROWSE)) {
                            it.browse(URI("https://appengers.netlify.app/help"))
                        }
                    }
                }) {
                Icon(Icons.Outlined.Info, contentDescription = "Info", modifier=Modifier.size(32.dp))
            }
        }
    }
}
