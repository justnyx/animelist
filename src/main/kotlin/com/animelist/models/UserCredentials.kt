package com.animelist.models

import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class UserCredentials(
    val username: String,
    val password: String
) {
    //hashes the password
    fun hashedPassword(): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    //checks if the username and password's format are right
    //returns true if the username is at least 3 characters and the password is at least 8 characters
    fun isValidCredentials(): Boolean {
        return username.length >= 3 && password.length >= 8
    }
}
