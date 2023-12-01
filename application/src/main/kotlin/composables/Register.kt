package composables
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Info
import apiClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.awt.Desktop
import java.net.URI
import java.util.*

// Enumeration to represent user roles
enum class Role {
    Student,
    Professor
}

// Composable function for the registration page
@Composable
fun registrationPage(onRegistrationSuccessful: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(Role.Student) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    ErrorDialog(showDialog = showErrorDialog, onDismiss = { showErrorDialog = false }, errorMessage = errorMessage)

    fun performRegistration() {
        coroutineScope.launch {
            // Perform registration request and handle the response
            val token = apiClient.registerRequest(
                email.trim(),
                password.trim(),
                firstName.trim(),
                lastName.trim(),
                role.toString().lowercase(Locale.getDefault()).trim()
            )
            // Check if registration was successful
            println(token)
            if (token != "User Exists Already") {
                onRegistrationSuccessful()
            } else {
                errorMessage = "Email exists already. Please sign in."
                showErrorDialog = true

            }
            email = ""
            password = ""
            // Reset other fields if needed
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header Text for the registration page
        Text(
            text = "Registration",
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

        // First name input field
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Last name input field
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password input field (custom composable)
        passwordField(
            password = password,
            onPasswordChange = { password = it },
            onEnterPress = { performRegistration() }

        )
        Spacer(modifier = Modifier.height(16.dp))

        // DropdownTextField for selecting the user's role
        dropdownTextField(
            role = role,
            onRoleChange = { role = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Row for Back and Register buttons
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

            // Register Button
            Button(
                onClick = {
                    performRegistration()
                }
            ) {
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
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            // Waiting for an Enter key press event...
            // You can implement a channel to listen for the key press if necessary
        }
    }
}

// Composable function for the DropdownTextField used for selecting user roles
@Composable
fun dropdownTextField(role: Role, onRoleChange: (Role) -> Unit) {

    // State variables for dropdown visibility and selected text
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(role.name) }

    // TextField with dropdown functionality
    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true, // Prevent manual input
        placeholder = {
            Text("Choose your role", color = Color.Gray) // Set the prompt text
        },
        trailingIcon = {
            IconButton(onClick = { expanded = true }) {
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }
    )
    // DropdownMenu to display role options
    if (expanded) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            for (item in Role.values()) {
                // DropdownMenuItem for each role option
                DropdownMenuItem(onClick = {
                    onRoleChange(item)
                    text = item.name
                    expanded = false
                }) {
                    Text(text = item.name)
                }
            }
        }
    }
}

