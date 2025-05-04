package org.example.logic.repository

import org.example.entity.AuditLogEntity
import java.util.*

interface AuditLogRepository {
    fun addAudit(auditLogEntity: AuditLogEntity)
    fun getProjectHistory(projectId: UUID): List<AuditLogEntity>
    fun getTaskHistory(taskId: UUID): List<AuditLogEntity>
}