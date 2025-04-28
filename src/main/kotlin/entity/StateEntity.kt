package org.example.entity

import java.util.UUID

data class StateEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val projectId: UUID
)
