package org.example.data.remote.dto

import java.util.UUID

data class ProjectDto(
    val id: UUID,
    val name: String,
    val createdByAdminId: String,
    val createdAt: String
)