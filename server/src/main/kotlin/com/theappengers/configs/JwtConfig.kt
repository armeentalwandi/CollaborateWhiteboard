package com.theappengers.configs

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.theappengers.schemas.User
import io.github.cdimascio.dotenv.Dotenv
import java.util.*

object JwtConfig {
    private var SECRET = "74f6b4e8f89576c09f26f38d86ca1836f92158d836262df7db5bfd444e8e7243"
    private const val ISSUER = "theappengers.io"
    private const val VALIDITY_IN_MS = 36_000_00 * 10 // 10 hours
    private val ALGORITHM = Algorithm.HMAC512(SECRET)

    val verifier = JWT.require(ALGORITHM).withIssuer(ISSUER).build()

    fun makeToken(user: User): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(ISSUER)
        .withClaim("id", user.userId.toString())
        .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_IN_MS))
        .sign(ALGORITHM)
}