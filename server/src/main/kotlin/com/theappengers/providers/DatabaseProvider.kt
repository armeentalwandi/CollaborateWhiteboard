package com.theappengers.providers

import com.theappengers.ENVIRONMENT
import com.theappengers.schemas.RoomsTable
import com.theappengers.schemas.RoomsToUsersTable
import com.theappengers.schemas.StrokesTable
import com.theappengers.schemas.UsersTable
import io.github.cdimascio.dotenv.Dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sound.midi.SysexMessage

class DatabaseProvider() {

    fun init(){

//        val dotenv = Dotenv.configure().directory("server/").load()

        var dbUrl = ""
        var driver = ""
        var dbUser = ""
        var dbPassword = ""

        if (ENVIRONMENT == "local") {
            dbUrl = "jdbc:sqlite:database.sqlite"
            driver = "org.sqlite.JDBC"
            dbUser = "admin"
            dbPassword = "admin"
        } else if (ENVIRONMENT == "remote") {
            dbUrl = "jdbc:postgresql://35.223.21.114:5432/postgres"
            driver = "org.postgresql.Driver"
            dbUser = "admin"
            dbPassword = "admin"
        }

        println(dbUrl)
        println(driver)
        println(dbUser)
        println(dbPassword)


        Database.connect(
            url = dbUrl,
            user = dbUser,
            password = dbPassword,
            driver = driver
        )
        transaction {
            SchemaUtils.create(StrokesTable)
            SchemaUtils.create(UsersTable)
            SchemaUtils.create(RoomsTable)
            SchemaUtils.create(RoomsToUsersTable)
        }
    }
}