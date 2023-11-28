package composables

import Room
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import models.AppData
import apiClient
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import models.Stroke
import java.util.*

@Composable
fun roomsDashboard(appData: AppData, onSignOut: () -> Unit, onGoToWhiteboard: () -> Unit) {
    val rooms = remember { mutableStateListOf<Room>() }
    var roomName by remember { mutableStateOf("") }
    var roomCode by remember { mutableStateOf("") }

    LaunchedEffect(key1 = appData.user?.userId) {
        val response = apiClient.getUserRooms(appData.user!!.userId)
        rooms.clear()
        response?.let {
            rooms.addAll(it)
        }
    }

    fun generateRandomRoomCode(length: Int = 6): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random(Random) }
            .joinToString("")
    }

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

        // Display rooms
        rooms.forEach { room ->
            Button(onClick = {
                appData.currRoom = room
                onGoToWhiteboard()
            }) {
                Text(text = "Room: ${room.roomName}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // roomName input field
        OutlinedTextField(
            value = roomName,
            onValueChange = { roomName = it },
            label = { Text("Room Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            runBlocking {
                launch {
                    val response = apiClient.createRoom(roomName, generateRandomRoomCode(), UUID.fromString(appData.user!!.userId))
                    if (response.status == HttpStatusCode.Created) {
                        val createdRoom = Json.decodeFromString<Room>(response.bodyAsText())
                        appData.currRoom = createdRoom
                        onGoToWhiteboard()
                    }
                }
            }
        }) {
            Text("Create New Room")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // roomCode input field
        OutlinedTextField(
            value = roomCode,
            onValueChange = { roomCode = it },
            label = { Text("Room Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to navigate to the whiteboard
        Button(
            onClick = {
                if (roomCode.isNotBlank()) {
                    runBlocking {
                        launch {
                            try {
                                val foundRoom = apiClient.findRoomByCode(roomCode)
                                if (foundRoom != null) {
                                    appData.currRoom = foundRoom
                                    onGoToWhiteboard()
                                } else {
                                    // show message pop-up to say that room wasn't found
                                }
                            } catch (e: Exception) {
                                // Handle other exceptions like network errors, bad requests, etc in a pop-up
                            }
                        }
                    }
                } else {
                    // show error message pop-up to not leave room code blank
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Join Room")
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
