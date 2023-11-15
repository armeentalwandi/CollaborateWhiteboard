package composables
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import apiClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


// Composable function for the login page
@Composable
fun loginPage(onLoginSuccessful: () -> Unit, onBack: () -> Unit, onNoAccount: () -> Unit) {
    // State variables for email and password
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password input field (custom composable)
        passwordField(
            password = password,
            onPasswordChange = { password = it }
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
                    // Handle login logic here
                    // Use coroutineScope to make an authentication request
                    runBlocking {
                        launch {
                            // Perform authentication request and get a token
                            val token = apiClient.loginRequest(email.trim(), password.trim())

                            // Check if authentication was successful
                            if (token != "Invalid Credentials"){
                                onLoginSuccessful()
                            }
                            email = ""
                            password = ""
                        }
                    }

                }
            ) {
                Text(text = "Login")
            }
        }
    }
}

// Composable function for the PasswordField
@Composable
fun passwordField(password: String, onPasswordChange: (String) -> Unit) {
    // State variable to toggle password visibility
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column {
        // Password input field with visibility toggle
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
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


