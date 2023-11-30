package composables

import Room
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import models.AppData
import apiClient
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.awt.Desktop
import java.net.URI
import java.util.*


@Composable
fun roomsDashboard(appData: AppData, onSignOut: () -> Unit, onGoToWhiteboard: () -> Unit) {
    val rooms = remember { mutableStateListOf<Room>() }
    var roomName by remember { mutableStateOf("") }
    var roomCode by remember { mutableStateOf("") }

    val removedRoomCodes = remember { mutableSetOf<String>() }

    val coroutineScope = rememberCoroutineScope()


    fun hideRoom(room: Room) {
        coroutineScope.launch {
            try {
                val response = apiClient.removeUserFromRoom(room.roomId, appData.user!!.userId)
                if (response.status == HttpStatusCode.OK) {
                    println("here")
                    // Remove the room from the UI
                    rooms.remove(room)
                    removedRoomCodes.add(room.roomCode)
                } else {
                    println(response.status)
                    println("here2")
                    // Handle error - e.g., show a snackbar or dialog
                }
            } catch (e: Exception) {
                // Handle exceptions - e.g., show a snackbar or dialog
            }
        }
    }

    // Load rooms once at the beginning
    LaunchedEffect(key1 = appData.user?.userId) {
        val response = apiClient.getUserRooms(appData.user!!.userId)
        rooms.clear()
        response.let {
            // Filter out rooms that have been removed locally
            rooms.addAll(it.filterNot { room -> room.roomCode in removedRoomCodes })
        }
    }

    Column() {
        TopBar(userName = appData.user?.first_name ?: "User", onSignOut = onSignOut)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally // Centers the children horizontally
            ) {


                Text("Current Rooms", style = MaterialTheme.typography.h4)


                LazyRow(
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(rooms) { room ->
                        RoomCard(room, onClick = {
                            appData.currRoom = room
                            onGoToWhiteboard()
                        }, onCloseClick = { hideRoom(room)})
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Text("Create New Room", style = MaterialTheme.typography.h4)
                Spacer(modifier = Modifier.height(27.dp))


                CreateRoomSection(roomName, onRoomNameChanged = { roomName = it }, onCreateRoom = {  runBlocking {
                    launch {
                        val response = apiClient.createRoom(roomName, generateRandomRoomCode(), UUID.fromString(appData.user!!.userId))
                        if (response.status == HttpStatusCode.Created) {
                            val createdRoom = Json.decodeFromString<Room>(response.bodyAsText())
                            appData.currRoom = createdRoom
                            onGoToWhiteboard()
                        }
                    }
                }
                })
            }
        }
        JoinRoomSection(roomCode, onRoomCodeChanged = { roomCode = it },
            onJoinRoom = {  if (roomCode.isNotBlank()) {
            runBlocking {
                launch {
                    try {
                        if (roomCode in removedRoomCodes) {
                            removedRoomCodes.remove(roomCode)
                        } else {
                            val foundRoom = apiClient.findRoomByCode(roomCode)

                            if (foundRoom != null) {
                                appData.currRoom = foundRoom
                                onGoToWhiteboard()
                            } else {
                                // show message pop-up to say that room wasn't found
                            }
                        }
                    } catch (e: Exception) {
                        // Handle other exceptions like network errors, bad requests, etc in a pop-up
                    }
                }
            }
        } else {
            // show error message pop-up to not leave room code blank
        }
        })
    }
}




@Composable
fun TopBar(userName: String, onSignOut: () -> Unit) {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primaryVariant)
            .padding(8.dp)
            .height(128.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Welcome back $userName",
            style = MaterialTheme.typography.h4,
            color = Color.White
        )
        Row {
            IconButton(
                onClick = {
                    desktop?.let {
                        if (it.isSupported(Desktop.Action.BROWSE)) {
                            it.browse(URI("https://appengers.netlify.app/help"))
                        }
                    }
                }) {
                Icon(Icons.Outlined.Info, contentDescription = "Info", tint = Color.White, modifier=Modifier.size(32.dp))
            }
            IconButton(onClick = onSignOut) {
                Icon(Icons.Filled.ExitToApp, contentDescription = "Sign out", tint = Color.White, modifier=Modifier.size(32.dp))
            }
        }
    }
}
@Composable
fun RoomCard(room: Room, onClick: () -> Unit, onCloseClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(width = 180.dp, height = 100.dp)
            .clickable { onClick() },
        elevation = 4.dp
    ) {
        Box {
            Text(
                text = "Name: ${room.roomName}\nRoom Code: ${room.roomCode}",
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.align(Alignment.Center)
            )

            IconButton(
                onClick = { onCloseClick(room.roomCode) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color.Red.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}

fun generateRandomRoomCode(length: Int = 6): String {
    val allowedChars = ('A'..'Z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random(Random) }
        .joinToString("")
}

@Composable
fun CreateRoomSection(roomName: String, onRoomNameChanged: (String) -> Unit, onCreateRoom: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = roomName,
            onValueChange = onRoomNameChanged,
            label = { Text("Room Name") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.height(40.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = onCreateRoom) {
            Text("Create New Room")


        }
    }
}



@Composable
fun JoinRoomSection(roomCode: String, onRoomCodeChanged: (String) -> Unit, onJoinRoom: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Join Existing Room", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = roomCode,
            onValueChange = onRoomCodeChanged,
            label = { Text("Room Code") },
            singleLine = true,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onJoinRoom,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text("Join Existing Room")
        }
    }
}