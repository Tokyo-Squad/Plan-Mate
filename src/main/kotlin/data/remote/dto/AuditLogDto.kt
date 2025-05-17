package org.example.data.remote.dto

import java.util.UUID

data class AuditLogDto(
    val id: UUID,
    val userId: UUID,
    val entityType: String,
    val entityId: UUID,
    val action: String,
    val changeDetails: String,
    val timestamp: String
)
