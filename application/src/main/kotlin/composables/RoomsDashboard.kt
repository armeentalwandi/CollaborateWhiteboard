package composables

import Room
import User
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import models.UWCourseData
import models.UWSubjectData
import models.UWTermData
import java.awt.Desktop
import java.net.URI
import java.util.*

@Composable
fun roomsDashboard(appData: AppData, onSignOut: () -> Unit, onGoToWhiteboard: () -> Unit) {
    val rooms = remember { mutableStateListOf<Room>() }
    var currentTerm = remember { mutableStateOf<UWTermData?>(null) }
    var subjects = remember { mutableStateListOf<UWSubjectData>() }
    var roomName by remember { mutableStateOf("") }
    var roomCode by remember { mutableStateOf("") }
    val removedRoomCodes = remember { mutableSetOf<String>() }
    val coroutineScope = rememberCoroutineScope()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    ErrorDialog(showDialog = showErrorDialog, onDismiss = { showErrorDialog = false }, errorMessage = errorMessage)


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

    LaunchedEffect(key1 = appData.user?.userId) {
        // Load rooms once at the beginning
        val userRooms = apiClient.getUserRooms(appData.user!!.userId)
        rooms.clear()
        userRooms.let {
            // Filter out rooms that have been removed locally
            rooms.addAll(it.filterNot { room -> room.roomCode in removedRoomCodes })
        }

        // get the current term info
        val currTerm = apiClient.getCurrentTermData()
        currentTerm.value = currTerm

        // get a list of all subjects
        val subjectList = apiClient.getSubjects()
        subjects.clear()
        subjects.addAll(subjectList)
    }

    Column() {
        TopBar(userName = appData.user?.first_name ?: "User",userRole = appData.user?.auth_level ?: "unknown", onSignOut = onSignOut)

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

                Text("Create Standard Room", style = MaterialTheme.typography.h4)
                Spacer(modifier = Modifier.height(27.dp))

                CreateRoomSection(
                    roomName,
                    onRoomNameChanged = { roomName = it },
                    onCreateRoom = {
                        runBlocking {
                            launch {
                                if (roomName == "") {
                                    errorMessage = "Please enter a room name."
                                    showErrorDialog = true // Show the error dialog
                                } else {
                                    val response = apiClient.createRoom(
                                        roomName,
                                        generateRandomRoomCode(),
                                        UUID.fromString(appData.user!!.userId)
                                    )
                                    if (response.status == HttpStatusCode.Created) {
                                        val createdRoom = Json.decodeFromString<Room>(response.bodyAsText())
                                        appData.currRoom = createdRoom
                                        onGoToWhiteboard()
                                    }
                                }
                            }
                        }
                    })
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally // Centers the children horizontally
            ) {
                JoinRoomSection(roomCode, onRoomCodeChanged = { roomCode = it },
                    onJoinRoom = {
                        if (roomCode.isNotBlank()) {
                            runBlocking {
                                launch {
                                    try {
                                        if (roomCode in removedRoomCodes) {
                                            removedRoomCodes.remove(roomCode)
                                        } else {
                                            val foundRoom = apiClient.findRoomByCode(roomCode)

                                            if (foundRoom != null) {

                                                apiClient.addUserToRoom(UUID.fromString(foundRoom.roomId), UUID.fromString(appData.user!!.userId))

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
            if (appData.user?.auth_level != "student") {
                Column(
                    modifier = Modifier.weight(1f).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // Centers the children horizontally
                ) {
                    CreateCourseRoomSection(
                        currentTerm = currentTerm.value,
                        subjects = subjects,
                        user = appData.user,
                        onCreateCourseRoom = { roomName, roomCode ->
                            runBlocking {
                                launch {
                                    val response =
                                        apiClient.createRoom(roomName, roomCode, UUID.fromString(appData.user!!.userId))
                                    if (response.status == HttpStatusCode.Created) {
                                        val createdRoom = Json.decodeFromString<Room>(response.bodyAsText())
                                        appData.currRoom = createdRoom
                                        onGoToWhiteboard()
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(userName: String, userRole: String, onSignOut: () -> Unit) {
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
        // Nesting a Column for the welcome message and user role
        Column {
            Text(
                "Welcome back, $userName!",
                style = MaterialTheme.typography.h4,
                color = Color.White
            )
            Text(
                "Current Authorization: $userRole",
                style = MaterialTheme.typography.subtitle1,
                color = Color.White
            )
        }

        // Action buttons
        Row {
            IconButton(
                onClick = {
                    desktop?.let {
                        if (it.isSupported(Desktop.Action.BROWSE)) {
                            it.browse(URI("https://appengers.netlify.app/help"))
                        }
                    }
                }) {
                Icon(Icons.Outlined.Info, contentDescription = "Info", tint = Color.White, modifier = Modifier.size(32.dp))
            }
            IconButton(onClick = onSignOut) {
                Icon(Icons.Filled.ExitToApp, contentDescription = "Sign out", tint = Color.White, modifier = Modifier.size(32.dp))
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
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = room.roomName,
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
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = roomName,
            onValueChange = onRoomNameChanged,
            label = { Text("Room Name") },
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCreateRoom,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text("Create Standard Room")
        }
    }
}

@Composable
fun CreateCourseRoomSection(
    user: User?,
    currentTerm: UWTermData?,
    subjects: List<UWSubjectData>,
    onCreateCourseRoom: (String, String) -> Unit
) {
    var selectedSubject by remember { mutableStateOf<UWSubjectData?>(null) }
    var selectedCourse by remember { mutableStateOf<UWCourseData?>(null) }
    var subjectsDropdownExpanded by remember { mutableStateOf(false) }
    var coursesDropdownExpanded by remember { mutableStateOf(false) }
    var courses = remember { mutableStateListOf<UWCourseData>() }
    val coroutineScope = rememberCoroutineScope()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    ErrorDialog(showDialog = showErrorDialog, onDismiss = { showErrorDialog = false }, errorMessage = errorMessage)

    Column {
        Text("Create Course Room", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Subjects Dropdown
            Box(modifier = Modifier.weight(1f)) {
                Text(text = selectedSubject?.code ?: "Select Subject")
                IconButton(onClick = { subjectsDropdownExpanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
                DropdownMenu(
                    expanded = subjectsDropdownExpanded,
                    onDismissRequest = { subjectsDropdownExpanded = false }
                ) {
                    subjects.forEach { subject ->
                        DropdownMenuItem(onClick = {
                            selectedSubject = subject
                            subjectsDropdownExpanded = false
                            println("Selected Subject: $selectedSubject")
                            coroutineScope.launch {
                                // Assuming currentTerm holds the current term data
                                currentTerm?.termCode?.let { termCode ->
                                    // Fetch courses for the selected subject
                                    courses.clear()
                                    val courseList = apiClient.getCourses(termCode, subject.code).sortedBy { it.catalogNumber }
                                    courses.addAll(courseList)
                                    if (courses.isNullOrEmpty()) {
                                        errorMessage = "No courses found for this subject this term."
                                        showErrorDialog = true // Show the error dialog
                                    }
                                }
                            }
                        }) {
                            Text(subject.code)
                        }
                    }
                }
            }

            // Courses Dropdown, enabled only if a subject is selected
            Box(modifier = Modifier.weight(1f)) {
                Text(text = selectedCourse?.let { "${it.subjectCode} ${it.catalogNumber}" } ?: "Select Course")
                IconButton(
                    onClick = { coursesDropdownExpanded = true },
                    enabled = selectedSubject != null && courses.isNotEmpty()
                ) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
                DropdownMenu(
                    expanded = coursesDropdownExpanded,
                    onDismissRequest = { coursesDropdownExpanded = false }
                ) {
                    courses.filter { it.subjectCode == selectedSubject?.code }.forEach { course ->
                        DropdownMenuItem(onClick = {
                            selectedCourse = course
                            coursesDropdownExpanded = false
                            println("Selected Course: $selectedCourse")
                        }) {
                            Text("${course.subjectCode} ${course.catalogNumber}")
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val roomName = "${selectedSubject?.code} ${selectedCourse?.catalogNumber} - ${currentTerm!!.name}"
                val roomCode = "${selectedSubject?.code}${selectedCourse?.catalogNumber}${currentTerm!!.termCode}"
                if (roomName.isNotBlank() && roomCode.isNotBlank()) {
                    if (user!!.auth_level == "professor") {
                        onCreateCourseRoom(roomName, roomCode)
                    } else {
                        errorMessage = "You must be a Professor to create Course Rooms."
                        showErrorDialog = true // Show the error dialog
                    }
                }
            },
            enabled = selectedSubject != null && selectedCourse != null
        ) {
            Text("Create Course Room")
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