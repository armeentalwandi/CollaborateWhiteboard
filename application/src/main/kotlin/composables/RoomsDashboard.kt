package composables

import Room
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    // Load rooms once at the beginning
    LaunchedEffect(key1 = appData.user?.userId) {
        val response = apiClient.getUserRooms(appData.user!!.userId)
        rooms.clear()
        response.let {
            rooms.addAll(it)
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
                        appData.currRoom = room
                        RoomCard(room, onClick = { onGoToWhiteboard() })
                    }
                }
            }


        }
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
fun RoomCard(room: Room, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .size(width = 180.dp, height = 100.dp) // Increase size as needed
            .clickable { onClick() },
        elevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(room.roomName, style = MaterialTheme.typography.subtitle1)
        }
    }
}
