package com.theappengers.providers

import com.theappengers.configs.DatabaseConfig
import io.github.cdimascio.dotenv.Dotenv
import org.jetbrains.exposed.sql.Database

class DatabaseProvider() {

    fun init(){
        val dotenv = Dotenv.configure().directory("server/").load()
        val dbUrl = dotenv["DATABASE_URL"]
        val dbUser = dotenv["DATABASE_USER"]
        val dbPassword = dotenv["DATABASE_PASSWORD"]
        Database.connect(
            url = dbUrl,
            driver = "org.sqlite.JDBC"
        )
    }
}