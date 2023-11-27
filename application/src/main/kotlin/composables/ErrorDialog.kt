// ErrorDialog.kt
package composables
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ErrorDialog(showDialog: Boolean, onDismiss: () -> Unit, errorMessage: String) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }
}
