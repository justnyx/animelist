package com.animelist.utils

import com.animelist.models.User
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.config.*
import java.util.*

class TokenManager (config: HoconApplicationConfig) {
    private val audience = config.property("audience").getString()
    private val secret = config.property("secret").getString()
    private val issuer = config.property("issuer").getString()
    private val expirationDate = System.currentTimeMillis() + 3600000 //the token expires after 1 hour
    fun generateJWT(user: User): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("username", user.username)
            .withClaim("userId", user.id)
            .withExpiresAt(Date(expirationDate))
            .sign(Algorithm.HMAC256(secret))
    }

    fun verifyJWT(): JWTVerifier {
        return JWT.require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
    }
}





