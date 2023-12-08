package com.animelist.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val password: String,
    val animeList: List<Anime>
)
