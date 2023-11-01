package com.theappengers

import org.jetbrains.exposed.dao.id.IntIdTable

object StrokesTable : IntIdTable() {
    val serializedStroke = text("serializedStroke")
}
