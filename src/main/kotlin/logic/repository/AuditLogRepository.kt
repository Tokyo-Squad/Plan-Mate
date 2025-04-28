package org.example.logic.repository

import org.example.entity.AuditLogEntity

interface AuditLogRepository {
    fun addAudit(auditLogEntity: AuditLogEntity)
    fun getProjectHistory(projectId: Int): List<AuditLogEntity>
    fun getTaskHistory(taskId: Int): List<AuditLogEntity>
    fun ensureAuditCsvExists(filePath: String): Boolean
}