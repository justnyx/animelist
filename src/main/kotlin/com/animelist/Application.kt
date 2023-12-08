package com.animelist

import com.animelist.plugins.*
import com.animelist.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        extracted()
    }.start(wait = true)
}

private fun Application.extracted() {
    val config = HoconApplicationConfig(ConfigFactory.load())
    val tokenManager = TokenManager(config)

    install(Authentication) {
        jwt {
            verifier(tokenManager.verifyJWT())
            realm = config.property("realm").getString()
            validate { jwtCredential ->
                if(jwtCredential.payload.getClaim("username").asString().isNotEmpty()) {
                    JWTPrincipal(jwtCredential.payload)
                } else {
                    null
                }
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }

    configureRouting()
}

