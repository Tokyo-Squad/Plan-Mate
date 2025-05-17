package org.example.data.util.mapper

import kotlinx.datetime.LocalDateTime
import org.bson.Document
import org.example.data.remote.dto.AuditLogDto
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import java.util.UUID


fun AuditLogDto.toAuditLogEntity(): AuditLogEntity = AuditLogEntity(
    id = id,
    userId = userId,
    entityType = AuditedEntityType.valueOf(entityType),
    entityId = entityId,
    action = AuditAction.valueOf(action),
    changeDetails = changeDetails,
    timestamp = timestamp.let { LocalDateTime.parse(it) }
)


fun AuditLogEntity.toAuditLogDto(): AuditLogDto = AuditLogDto(
    id = id,
    userId = userId,
    entityType = entityType.toString(),
    entityId = entityId,
    action = action.toString(),
    changeDetails = changeDetails,
    timestamp = timestamp.toString()
)

fun AuditLogDto.toDocument(): Document = Document().apply {
    put("id", id)
    put("userId", userId)
    put("entityType", entityType)
    put("entityId", entityId)
    put("action", action)
    put("changeDetails", changeDetails)
    put("timestamp", timestamp)
}

fun Document.toAuditLogDto(): AuditLogDto = AuditLogDto(
    id = UUID.fromString(getString("id")),
    userId = UUID.fromString(getString("userId")),
    entityType = getString("entityType"),
    entityId = UUID.fromString(getString("entityId")),
    action = getString("action"),
    changeDetails = getString("changeDetails"),
    timestamp = getString("timestamp")
)

