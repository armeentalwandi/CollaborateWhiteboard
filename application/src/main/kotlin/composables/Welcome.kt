import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material.*
import androidx.compose.ui.Modifier

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
        }
    }
}
