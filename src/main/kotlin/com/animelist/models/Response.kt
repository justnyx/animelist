package com.animelist.models

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val message: String,
    val success: Boolean
)
