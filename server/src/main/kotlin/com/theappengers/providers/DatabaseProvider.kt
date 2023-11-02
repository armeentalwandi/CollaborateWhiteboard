package com.theappengers.providers

import com.theappengers.schemas.StrokesTable
import com.theappengers.schemas.UsersTable
import io.github.cdimascio.dotenv.Dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

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
        transaction {
            SchemaUtils.create(StrokesTable)
            SchemaUtils.create(UsersTable)

        }
    }
}