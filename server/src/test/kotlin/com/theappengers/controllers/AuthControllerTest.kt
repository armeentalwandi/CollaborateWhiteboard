package com.theappengers.controllers

import LoginRequest
import RegisterRequest
import com.theappengers.schemas.User
import com.theappengers.services.JwtService
import com.theappengers.services.UsersService
import io.ktor.http.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.reset
import java.lang.reflect.Field
import java.util.UUID

class AuthControllerTest {

    private lateinit var controller: AuthController
    private lateinit var mockUsersService: UsersService
    private lateinit var mockJwtService: JwtService
    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        controller = AuthController()
        mockUsersService = Mockito.mock(UsersService::class.java)
        mockJwtService = Mockito.mock(JwtService::class.java)

        // Inject mock using reflection
        val field: Field = AuthController::class.java.getDeclaredField("usersService")
        field.isAccessible = true
        field.set(controller, mockUsersService)

        // Inject mock using reflection
        val field2: Field = AuthController::class.java.getDeclaredField("jwtService")
        field2.isAccessible = true
        field2.set(controller, mockJwtService)

        user = User(UUID.randomUUID(), "FirstName", "LastName", "authLevel", "user@example.com", "hashedPassword")
    }

    @AfterEach
    fun tearDown() {
        reset(mockUsersService, mockJwtService)
    }
    @Test
    fun login_shouldReturnOKonValidCredentials() {
        // Arrange
        val loginRequest = LoginRequest("user@example.com", "password")
        `when`(mockUsersService.doesEmailExist(loginRequest.email)).thenReturn(true)
        `when`(mockUsersService.findUserByEmail(loginRequest.email)).thenReturn(user)
        `when`(mockUsersService.isValidPassword(loginRequest.password, user.hashedPassword)).thenReturn(true)
        `when`(mockJwtService.makeToken(user)).thenReturn("token")

        // Act
        val result = controller.login(loginRequest)

        // Assert
        assertEquals(HttpStatusCode.OK, result.first)
        assertNotNull(result.second.token)
    }

    @Test
    fun login_shouldReturnUnauthorizedOnInvalidCredentials() {
        // Arrange
        val loginRequest = LoginRequest("user@example.com", "wrongPassword")
        val user = User(UUID.randomUUID(), "FirstName", "LastName", "authLevel", loginRequest.email, "hashedPassword")
        `when`(mockUsersService.doesEmailExist(loginRequest.email)).thenReturn(true)
        `when`(mockUsersService.findUserByEmail(loginRequest.email)).thenReturn(user)
        `when`(mockUsersService.isValidPassword(loginRequest.password, user.hashedPassword)).thenReturn(false)

        // Act
        val result = controller.login(loginRequest)

        // Assert
        assertEquals(HttpStatusCode.Unauthorized, result.first)
    }

    @Test
    fun login_shouldReturnBadRequestOnInvalidEmail() {
        // Arrange
        val loginRequest = LoginRequest("invalid@example.com", "password")
        `when`(mockUsersService.doesEmailExist(loginRequest.email)).thenReturn(false)

        // Act
        val result = controller.login(loginRequest)

        // Assert
        assertEquals(HttpStatusCode.BadRequest, result.first)
    }

    @Test
    fun register_shouldReturnOKonSuccessfulRegistration() {
        // Arrange
        val registerRequest = RegisterRequest("user@example.com", "password", "FirstName", "LastName", "authLevel")
        `when`(mockUsersService.doesEmailExist(registerRequest.email)).thenReturn(false)
        `when`(mockUsersService.hashPassword("password")).thenReturn("hashedPassword")
        `when`(mockUsersService.createUser(registerRequest.email, "hashedPassword", registerRequest.firstName, registerRequest.lastName, registerRequest.authLevel)).thenReturn(user)
        `when`(mockJwtService.makeToken(user)).thenReturn("token")

        // Act
        val result = controller.register(registerRequest)

        // Assert
        assertEquals(HttpStatusCode.OK, result.first)
        assertNotNull(result.second.token)
    }

    @Test
    fun register_shouldReturnBadRequestIfUserAlreadyExists() {
        // Arrange
        val registerRequest = RegisterRequest("user@example.com", "password", "FirstName", "LastName", "authLevel")
        `when`(mockUsersService.doesEmailExist(registerRequest.email)).thenReturn(true)

        // Act
        val result = controller.register(registerRequest)

        // Assert
        assertEquals(HttpStatusCode.BadRequest, result.first)
        assertEquals("User Exists Already", result.second.token)
    }

    @Test
    fun register_shouldReturnInternalServerErrorOnCreateUserFailure() {
        // Arrange
        val registerRequest = RegisterRequest("user@example.com", "password", "FirstName", "LastName", "authLevel")
        `when`(mockUsersService.doesEmailExist(registerRequest.email)).thenReturn(false)
        `when`(mockUsersService.hashPassword("password")).thenReturn("hashedPassword")
        `when`(mockUsersService.createUser(registerRequest.email, "hashedPassword", registerRequest.firstName, registerRequest.lastName, registerRequest.authLevel)).thenReturn(null)

        // Act
        val result = controller.register(registerRequest)

        // Assert
        assertEquals(HttpStatusCode.InternalServerError, result.first)
    }
}
