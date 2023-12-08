package com.animelist.database

import io.ktor.util.*
import org.ktorm.database.Database

object DatabaseConnection {
    val database = Database.connect(
        url = "jdbc:mysql://sql11.freemysqlhosting.net:3306/sql11668556",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "sql11668556",
        password = "fpF5JRkrhR"
    )
}