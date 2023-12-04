package com.theappengers.services

import com.theappengers.configs.JwtConfig
import com.theappengers.schemas.User

class JwtService {
    fun makeToken(user: User): String {
        return JwtConfig.makeToken(user)
    }
}