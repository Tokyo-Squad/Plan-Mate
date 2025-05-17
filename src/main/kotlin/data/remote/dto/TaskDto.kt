package org.example.data.remote.dto

import java.util.UUID

data class TaskDto(
    val id: UUID,
    val title: String,
    val description: String,
    val stateId: UUID,
    val projectId: UUID,
    val createdByUserId: UUID,
    val createdAt: String
)