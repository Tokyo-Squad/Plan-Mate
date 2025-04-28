package org.example.entity

import java.util.UUID

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val stateId: UUID,
    val projectId: UUID,
    val createdByUserId: UUID
)