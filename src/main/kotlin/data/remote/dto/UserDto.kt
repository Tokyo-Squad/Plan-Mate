package org.example.data.remote.dto

import java.util.UUID

data class UserDto(
    val id: UUID,
    val username: String,
    val password: String,
    val type: String
)