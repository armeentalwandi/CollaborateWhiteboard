package composables
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import apiClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.AppData
import androidx.compose.ui.input.key.*
import composables.*
import java.awt.Desktop
import java.awt.SystemColor.desktop
import java.net.URI


// Composable function for the login page
@Composable
fun loginPage(onLoginSuccessful: () -> Unit, onBack: () -> Unit, onNoAccount: () -> Unit, data: AppData) {
    // State variables for email and password
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    ErrorDialog(showDialog = showErrorDialog, onDismiss = { showErrorDialog = false }, errorMessage = errorMessage)

    fun performLogin() {
        coroutineScope.launch {
            val response = apiClient.loginRequest(email.trim(), password.trim())
            if (response.first != "Invalid Credentials"){
                data.user = response.second
                onLoginSuccessful()
            } else {
                errorMessage = "Invalid credentials. Please try again."
                showErrorDialog = true // Show the error dialog
                //ErrorDialog(showDialog = showErrorDialog, onDismiss = { showErrorDialog = false }, errorMessage = errorMessage)

            }
            email = ""
            password = ""
        }
    }


    // Column to layout components vertically
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header Text for the login page
        Text(
            text = "Login",
            style = MaterialTheme.typography.h4,
            color = Color.Blue
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Email input field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Enter) {
                    performLogin()
                    true // Indicate that the event has been consumed
                } else {
                    false
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password input field (custom composable)
        passwordField(
            password = password,
            onPasswordChange = { password = it },
            onEnterPress = { performLogin() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Text to navigate to registration page
        Text("Don't have an account? ", color = Color.Blue, modifier = Modifier.clickable { onNoAccount() })

        Spacer(modifier = Modifier.height(16.dp))

        // Row for Back and Login buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back Button
            Button(
                onClick = {
                    onBack() // Handle back navigation
                }
            ) {
                Text("Back")
            }

            // Login Button
            Button(
                onClick = {
                    performLogin()
                }
            ) {
                Text(text = "Login")
            }
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

// Composable function for the PasswordField
@Composable
fun passwordField(password: String, onPasswordChange: (String) -> Unit, onEnterPress: () -> Unit) {
    // State variable to toggle password visibility
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column {
        // Password input field with visibility toggle
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth().onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Enter) {
                    onEnterPress()
                    true // Indicate that the event has been consumed
                } else {
                    false
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )


        Spacer(modifier = Modifier.height(8.dp))

        // Row for checkbox and label to show/hide password
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Checkbox to toggle password visibility
            Checkbox(
                checked = isPasswordVisible,
                onCheckedChange = { isPasswordVisible = it }
            )
            Text(text = "Show password")
        }

    }

}



