package com.theappengers.schemas

import io.ktor.server.auth.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

data class User(var userId: UUID, var firstName: String, var lastName: String, var authLevel: String, var email: String, var hashedPassword: String)

object UsersTable : UUIDTable("Users") {
    val email = varchar("email", 255).uniqueIndex()
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val hashedPassword = varchar("hashed_password", 255)
    val authLevel = varchar("auth_level", 255)
}

fun UsersTable.findUserById(userId: UUID) : User? {
    var user: User? = null
    transaction {
        val row = UsersTable.select{ UsersTable.id eq userId }.single()
        user = User(
            userId = row[UsersTable.id].value,
            firstName = row[firstName],
            lastName = row[lastName],
            email = row[email],
            hashedPassword = row[hashedPassword],
            authLevel = row[authLevel]
        )
    }

    return user
}

fun UsersTable.findUserByEmail(email: String) : User? {
    var user: User? = null
    transaction {
        val row = UsersTable.select{ UsersTable.email eq email }.single()
        user = User(
            userId = row[UsersTable.id].value,
            firstName = row[firstName],
            lastName = row[lastName],
            email = row[UsersTable.email],
            hashedPassword = row[hashedPassword],
            authLevel = row[authLevel]
        )
    }

    return user
}

fun UsersTable.isValidPassword(inputPassword : String, storedHashedPassword: String): Boolean {
    return BCrypt.checkpw(inputPassword, storedHashedPassword)
}

fun UsersTable.hashPassword(password: String) : String {
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

fun UsersTable.createUser(email: String, hashedPassword: String, firstName: String, lastName: String, authLevel: String) : User? {
    var userRow : ResultRow? = null
    transaction {
        val generatedKey = UsersTable.insert {
            it[UsersTable.email] = email
            it[UsersTable.hashedPassword] = hashedPassword
            it[UsersTable.firstName] = firstName
            it[UsersTable.lastName] = lastName
            it[UsersTable.authLevel] = authLevel
        } get UsersTable.id

        userRow = UsersTable.select { UsersTable.id eq generatedKey }.singleOrNull()
    }

    return if (userRow != null) User(
        userId = userRow!![UsersTable.id].value,
        email = userRow!![UsersTable.email],
        hashedPassword = userRow!![UsersTable.hashedPassword],
        firstName = userRow!![UsersTable.firstName],
        lastName = userRow!![UsersTable.lastName],
        authLevel = userRow!![UsersTable.authLevel]

    ) else null
}

fun UsersTable.doesEmailExist(email: String): Boolean {
    return transaction {
        UsersTable.select { UsersTable.email eq email }.count() > 0
    }
}