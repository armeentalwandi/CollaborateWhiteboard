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
import apiClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

enum class Role {
    Student,
    Professor
}

@Composable
fun RegistrationPage(onRegistrationSuccessful: () -> Unit, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(Role.Student) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Registration",
            style = MaterialTheme.typography.h4,
            color = Color.Blue
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            password = password,
            onPasswordChange = { password = it }
        )
        Spacer(modifier = Modifier.height(16.dp))

        var selectedRole by remember { mutableStateOf(Role.Student) }

        DropdownTextField(
            role = selectedRole,
            onRoleChange = { selectedRole = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                    // Handle registration logic here

                    // Use coroutineScope to make an authentication request
                    runBlocking {
                        launch {
                            // Perform authentication request and get a token
                            val token = apiClient.registerRequest(email.trim(), password.trim(), firstName.trim(), lastName.trim(), role.toString()
                                .lowercase(Locale.getDefault())
                                .trim() )
                            // Check if authentication was successful
                            if (token != "Invalid Credentials"){
                                onRegistrationSuccessful()
                            }
                            email = ""
                            password = ""
                        }
                    }
                }
            ) {
                Text(text = "Register")
            }
        }
    }
}


//@Composable
//fun RegistrationPage(onRegistrationSuccessful: () -> Unit) {
//
//    var email by remember { mutableStateOf("") }
//    var firstName by remember { mutableStateOf("") }
//    var lastName by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var passwordConfirmation by remember { mutableStateOf("") }
//    var role by remember { mutableStateOf(Role.Student) }
//
//
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "Registration",
//            style = MaterialTheme.typography.h4,
//            color = Color.Blue
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = firstName,
//            onValueChange = { firstName = it },
//            label = { Text("First Name") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = lastName,
//            onValueChange = { lastName = it },
//            label = { Text("Last Name") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        PasswordField(
//            password = password,
//            onPasswordChange = { password = it }
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//
//        var selectedRole by remember { mutableStateOf(Role.Student) }
//
//        DropdownTextField(
//            role = selectedRole,
//            onRoleChange = { selectedRole = it }
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//                // Handle registration logic here
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "Register")
//        }
//
//
//    }
//}

@Composable
fun DropdownTextField(role: Role, onRoleChange: (Role) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(role.name) }

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

    if (expanded) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            for (item in Role.values()) {
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

