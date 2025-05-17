package fakeData

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import domain.model.AuditAction
import domain.model.AuditLog
import domain.model.AuditedType
import java.util.UUID

fun createAuditLogEntity(
    userId: UUID = UUID.randomUUID(),
    entityType: AuditedType = AuditedType.TASK,
    entityId: UUID = UUID.randomUUID(),
    action: AuditAction = AuditAction.CREATE,
    changeDetails: String = "Task created",
    timestamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
): AuditLog {
    return AuditLog(
        userId = userId,
        entityType = entityType,
        entityId = entityId,
        action = action,
        changeDetails = changeDetails,
        timestamp = timestamp
    )
}