package org.example.entity

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class AuditLogEntity(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val entityType: AuditedEntityType,
    val entityId: UUID,
    val action: AuditAction,
    val changeDetails: String,
    val timestamp: LocalDateTime
)

enum class AuditedEntityType {
    TASK,
    PROJECT,
    STATE
}

enum class AuditAction {
    CREATE,
    UPDATE,
    DELETE
}