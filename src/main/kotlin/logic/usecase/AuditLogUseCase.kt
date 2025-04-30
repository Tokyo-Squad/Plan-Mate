package org.example.logic.usecase

import org.example.entity.AuditLogEntity
import org.example.logic.repository.AuditLogRepository

class AuditLogUseCase(
    private val auditLogRepository: AuditLogRepository
) {

    fun addAuditLog(auditLogEntity: AuditLogEntity) {
        return auditLogRepository.addAudit(auditLogEntity)
    }

    fun getProjectAuditLogs(projectId: Int): List<AuditLogEntity> {
        return auditLogRepository.getProjectHistory(projectId)

    }

    fun getTaskAuditLogs(taskId: Int): List<AuditLogEntity> {
        return auditLogRepository.getTaskHistory(taskId)
    }

}