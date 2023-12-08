package com.animelist.entities

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

//stores user data in the same format as the database
object UserEntity: Table<Nothing> ("user") {
    val id = int("id").primaryKey()
    val username = varchar("username")
    val password = varchar("password")
    val animeList = varchar("anime_list")
}