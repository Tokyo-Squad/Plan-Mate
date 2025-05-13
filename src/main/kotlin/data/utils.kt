package org.example.data

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import java.util.*

suspend fun logAudit(
    userId: UUID,
    entityId: UUID,
    action: AuditAction,
    changeDetails: String,
    auditDataProvider:DataProvider<AuditLogEntity>
) {
    auditDataProvider.add(
        AuditLogEntity(
            userId = userId,
            entityType = AuditedEntityType.PROJECT,
            entityId = entityId,
            action = action,
            changeDetails = changeDetails,
            timestamp = Clock.System.now().toLocalDateTime(UTC)
        )
    )
}