package com.animelist.routing

import com.animelist.database.DatabaseConnection
import com.animelist.entities.UserEntity
import com.animelist.models.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.dsl.*

fun Application.registerRouting() {
    val db = DatabaseConnection.database

    routing {
        post("/register") {
            //get the user from the body of the request
            val userCredentials = call.receive<UserCredentials>()

            //if the credentials are not valid, responds with an error message and returns
            if(!userCredentials.isValidCredentials()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    Response(message = "Invalid username or password", success = false)
                )
                return@post
            }

            val username = userCredentials.username.lowercase()
            val password = userCredentials.hashedPassword()
            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.username eq username }
                .map { it[UserEntity.username] }
                .firstOrNull()

            //checks if a user already exists with the same username
            if(user != null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    Response(message = "Username is already in use", success = false)
                )
                return@post
            }

            //adds the new user to the database
            db.insert(UserEntity) {
                set(it.username, username)
                set(it.password, password)
                set(it.animeList, "")
            }

            call.respond(
                HttpStatusCode.Created,
                Response(message = "Registration Successful!", success = true)
            )
        }
    }
}