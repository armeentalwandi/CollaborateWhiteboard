package com.theappengers.controllers

import LoginRequest
import LoginResponse
import RegisterRequest
import RegisterResponse
import User
import com.theappengers.configs.JwtConfig
import com.theappengers.services.JwtService
import com.theappengers.services.UsersService
import io.ktor.http.*

class AuthController {

    private val usersService = UsersService()
    private val jwtService = JwtService()

    fun login(loginRequest: LoginRequest): Pair<HttpStatusCode, LoginResponse> {
        if (!usersService.doesEmailExist(loginRequest.email)) {
            return HttpStatusCode.BadRequest to LoginResponse("Invalid Credentials", null)
        }

        val user = usersService.findUserByEmail(loginRequest.email)

        return if (user != null && usersService.isValidPassword(loginRequest.password, user.hashedPassword)) {
            val token = jwtService.makeToken(user)
            HttpStatusCode.OK to LoginResponse(token, User(user.userId.toString(), user.email, user.firstName, user.lastName, user.authLevel))
        } else {
            HttpStatusCode.Unauthorized to LoginResponse("Invalid Credentials", null)
        }
    }

    fun register(registerRequest: RegisterRequest): Pair<HttpStatusCode, RegisterResponse> {
        if (usersService.doesEmailExist(registerRequest.email)) {
            return HttpStatusCode.BadRequest to RegisterResponse("User Exists Already")
        }

        val hashedPassword = usersService.hashPassword(registerRequest.password)
        val newUser = usersService.createUser(registerRequest.email, hashedPassword, registerRequest.firstName, registerRequest.lastName, registerRequest.authLevel)

        return if (newUser != null) {
            val token = jwtService.makeToken(newUser)
            HttpStatusCode.OK to RegisterResponse(token)
        } else {
            HttpStatusCode.InternalServerError to RegisterResponse("Error creating user")
        }
    }
}
