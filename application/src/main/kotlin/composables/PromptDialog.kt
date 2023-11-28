import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PromptDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    confirmButtonText: String
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        MaterialTheme {
            Surface(
                shape = MaterialTheme.shapes.medium, // This adds the rounded corners
                modifier = Modifier.wrapContentWidth()
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(24.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { onDismissRequest() },
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                        ) {
                            Text(
                                text = confirmButtonText,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
