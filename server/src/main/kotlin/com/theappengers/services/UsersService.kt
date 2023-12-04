package com.theappengers.services

import com.theappengers.schemas.*
import java.util.UUID

class UsersService {

    fun findUserById(userId: UUID): User? {
        return UsersTable.findUserById(userId)
    }

    fun findUserByEmail(email: String): User? {
        return UsersTable.findUserByEmail(email)
    }

    fun isValidPassword(inputPassword: String, storedHashedPassword: String): Boolean {
        return UsersTable.isValidPassword(inputPassword, storedHashedPassword)
    }

    fun hashPassword(password: String): String {
        return UsersTable.hashPassword(password)
    }

    fun createUser(email: String, hashedPassword: String, firstName: String, lastName: String, authLevel: String): User? {
        return UsersTable.createUser(email, hashedPassword, firstName, lastName, authLevel)
    }

    fun doesEmailExist(email: String): Boolean {
        return UsersTable.doesEmailExist(email)
    }
}
