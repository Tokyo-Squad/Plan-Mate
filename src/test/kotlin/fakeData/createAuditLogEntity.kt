package fakeData

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import java.util.UUID

fun createAuditLogEntity(
    userId: UUID = UUID.randomUUID(),
    entityType: AuditedEntityType = AuditedEntityType.TASK,
    entityId: UUID = UUID.randomUUID(),
    action: AuditAction = AuditAction.CREATE,
    changeDetails: String = "Task created",
    timestamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
): AuditLogEntity {
    return AuditLogEntity(
        userId = userId,
        entityType = entityType,
        entityId = entityId,
        action = action,
        changeDetails = changeDetails,
        timestamp = timestamp
    )
}