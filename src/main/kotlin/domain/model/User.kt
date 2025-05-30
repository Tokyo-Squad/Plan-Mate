package org.example.entity

import java.util.UUID

data class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val password: String,
    val type: UserType
)

enum class UserType {
    ADMIN, MATE
}

