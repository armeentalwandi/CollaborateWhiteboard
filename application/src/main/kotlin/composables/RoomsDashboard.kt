package composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.AppData

@Composable
fun roomsDashboard(appData: AppData, onSignOut: () -> Unit, onGoToWhiteboard: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the welcome message with the user's name
        Text(text = "Welcome back ${appData.user?.first_name ?: "User"}", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Button to navigate to the whiteboard
        Button(
            onClick = { onGoToWhiteboard() },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Go to Whiteboard")
        }

        // Button to sign out
        Button(
            onClick = {
                appData.user = null
                onSignOut()
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Sign Out")
        }
    }
}
