package org.example.logic.repository

import org.example.entity.AuditLogEntity
import java.util.*

interface AuditLogRepository {
    suspend fun addAudit(auditLogEntity: AuditLogEntity)
    suspend fun getProjectHistory(projectId: UUID): List<AuditLogEntity>
    suspend fun getTaskHistory(taskId: UUID): List<AuditLogEntity>
}