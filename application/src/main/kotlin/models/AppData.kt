package models

import Room
import User
import kotlinx.serialization.Serializable

@Serializable
class AppData(
    var user: User? = null,
    var room: Room? = null,
)
