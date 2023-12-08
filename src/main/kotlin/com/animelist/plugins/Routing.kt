package com.animelist.plugins

import com.animelist.routing.homeRouting
import com.animelist.routing.loginRouting
import com.animelist.routing.registerRouting
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Welcome to Anime List")
        }

        homeRouting()
        registerRouting()
        loginRouting()
    }
}


