package org.example.entity

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class ProjectEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val createdByAdminId: UUID,
    val createdAt: LocalDateTime,
)
