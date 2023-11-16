package com.theappengers.providers

import com.theappengers.schemas.RoomsTable
import com.theappengers.schemas.RoomsToUsersTable
import com.theappengers.schemas.StrokesTable
import com.theappengers.schemas.UsersTable
import io.github.cdimascio.dotenv.Dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseProvider() {

    fun init(){

        val dotenv = Dotenv.configure().directory("server/").load()

        val env = dotenv["ENV"]

        val dbUser = dotenv["DATABASE_USER"]
        val dbPassword = dotenv["DATABASE_PASSWORD"]
        var dbUrl = ""
        var driver = ""

        if (env == "local") {
            dbUrl = dotenv["DATABASE_URL"]
            driver = "org.sqlite.JDBC"
        } else if (env == "remote") {
            dbUrl = dotenv["CLOUDSQL_URL"]
            driver = "org.postgresql.driver"
        }

        Database.connect(
            url = dbUrl,
            user = dbUser,
            password = dbPassword,
            driver = "org.sqlite.JDBC"
        )
        transaction {
            SchemaUtils.create(StrokesTable)
            SchemaUtils.create(UsersTable)
            SchemaUtils.create(RoomsTable)
            SchemaUtils.create(RoomsToUsersTable)
        }
    }
}