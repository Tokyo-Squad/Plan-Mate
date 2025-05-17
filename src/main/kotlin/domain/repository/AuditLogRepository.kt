package org.example.logic.repository

import domain.model.AuditLog
import java.util.*

interface AuditLogRepository {
    suspend fun addAudit(auditLog: AuditLog)
    suspend fun getProjectHistory(projectId: UUID): List<AuditLog>
    suspend fun getTaskHistory(taskId: UUID): List<AuditLog>
}