package logic.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class AuditLog(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID,
    val entityType: AuditedType,
    val entityId: UUID,
    val action: AuditAction,
    val changeDetails: String,
    val timestamp: LocalDateTime
)

enum class AuditedType {
    TASK,
    PROJECT
}

enum class AuditAction {
    CREATE,
    UPDATE,
    DELETE
}