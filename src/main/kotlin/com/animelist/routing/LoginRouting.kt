package com.animelist.routing

import com.animelist.database.DatabaseConnection
import com.animelist.entities.UserEntity
import com.animelist.models.*
import com.animelist.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt

fun Application.loginRouting() {
    val db = DatabaseConnection.database
    val tokenManager = TokenManager(HoconApplicationConfig(ConfigFactory.load()))

    routing {
        post("/login") {
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
            val password = userCredentials.password
            val user = db.from(UserEntity)
                .select()
                .where { UserEntity.username eq username }
                .map {
                    val id = it[UserEntity.id]!!
                    val username1 = it[UserEntity.username]!!
                    val password1 = it[UserEntity.password]!!
                    val animeIds = it[UserEntity.animeList]!!
                    User(id, username1, password1, animeIds.toAnimeList())
                }
                .firstOrNull()

            //if the user does not exist, responds with an error message and returns
            if(user == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    Response(message = "Invalid username or password", success = false)
                )
                return@post
            }

            val doesPasswordMatch = BCrypt.checkpw(password, user.password)
            //if the password is not correct, responds with an error message and returns
            if(!doesPasswordMatch) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    Response(message = "Invalid username or password", success = false)
                )
            }

            //creates a JWT for the user
            val token = tokenManager.generateJWT(user)

            call.respond(
                HttpStatusCode.OK,
                Response(message = token, success = true)
            )
        }

        authenticate {
            get("/profile") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                //get the user's anime list, based on the database
                val animeList = db.from(UserEntity)
                    .select()
                    .where { UserEntity.username eq username }
                    .map {
                        val animeIds = it[UserEntity.animeList]!!
                        animeIds.toAnimeList()
                    }
                    .firstOrNull()

                call.respond(animeList!!)
            }

            put("/home/{id}") {
                val id = call.parameters["id"]?.toInt() ?: -1
                //get the watch status from the body of the request
                val watchStatus = call.receive<WatchStatusRequest>().watchStatus

                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                //get the user's anime list, based on the database
                var animeList: MutableList<Anime>? = db.from(UserEntity)
                    .select()
                    .where { UserEntity.username eq username }
                    .map {
                        val animeIds = it[UserEntity.animeList]!!
                        animeIds.toAnimeList()
                    }
                    .firstOrNull()
                    ?.toMutableList()

                //if the list is null or empty, creates a new list, with an anime, with id {id} and watch status watchStatus
                if(animeList.isNullOrEmpty()) {
                    animeList = mutableListOf(
                        Anime(id, "", "", 0, "",
                            "", 0, "", emptyList(), watchStatus)
                    )
                }
                //if the anime with id {id} is not in the list, then adds it
                else if(animeList.none { it.id == id }) {
                    animeList.add(Anime(id, "", "", 0, "",
                        "", 0, "", emptyList(), watchStatus))
                }
                //if the anime is in the list, updates its watch status
                else {
                    animeList.first {
                        it.id == id
                    }.watchStatus = watchStatus
                }

                val ids = animeList.toAnimeIds()

                //updates the database, with the new value
                val rowsAffected = db.update(UserEntity) {
                    set(it.animeList, ids)
                    where { it.username eq username }
                }

                //if exactly 1 row was affected, the success
                if(rowsAffected == 1) {
                    call.respond(
                        HttpStatusCode.OK,
                        Response(
                            message = "Anime added successfully!",
                            success = true
                        )
                    )
                }
                //otherwise, failure
                else {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        Response(
                            message = "Failed to add anime",
                            success = false
                        )
                    )
                }
            }
        }
    }
}