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
import androidx.compose.foundation.clickable

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
            modifier = Modifier.width(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // First name input field
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.width(300.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Last name input field
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.width(300.dp)
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
            modifier = Modifier.width(300.dp),
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

            // Register Button
            Button(
                onClick = {
                    performRegistration()
                }
            ) {
                Text(text = "Register")
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
    Box(modifier = Modifier.width(300.dp)) { // Set the width of the Box to match the TextField
        var expanded by remember { mutableStateOf(false) }
        var selectedRole by remember { mutableStateOf(role) }
        val anchor = Modifier.width(300.dp) // Ensure the DropdownMenu anchors to the full width

        TextField(
            value = selectedRole.name,
            onValueChange = { /* Ignored as the TextField is read-only */ },
            readOnly = true,
            modifier = anchor,
            label = { Text("Role") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Expand Dropdown",
                    modifier = Modifier.clickable { expanded = true }
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = anchor // Apply the anchor Modifier to DropdownMenu as well
        ) {
            Role.values().forEach { selection ->
                DropdownMenuItem(
                    onClick = {
                        selectedRole = selection
                        onRoleChange(selection)
                        expanded = false
                    }
                ) {
                    Text(text = selection.name)
                }
            }
        }
    }
}

