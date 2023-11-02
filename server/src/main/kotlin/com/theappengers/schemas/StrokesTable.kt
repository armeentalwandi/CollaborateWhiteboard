package com.theappengers.schemas

import org.jetbrains.exposed.sql.Table

object StrokesTable : Table() {
    val strokeId = uuid("strokeId").uniqueIndex() // UUID as primary key
    val userId = uuid("userId") // foreign key reference to UsersTable
    val serializedStroke = text("serializedStroke")
}
