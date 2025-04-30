package org.example.logic.usecase

import org.example.entity.AuditLogEntity
import org.example.logic.repository.AuditLogRepository

class AuditLogUseCase(
    private val auditLogRepository: AuditLogRepository
) {

    fun addAuditLog(auditLogEntity: AuditLogEntity) {
        requireNotNull(auditLogEntity.entityId) { "Entity ID cannot be null" }
        auditLogRepository.addAudit(auditLogEntity)
    }

    fun getProjectAuditLogs(projectId: Int): List<AuditLogEntity> {
        val history = auditLogRepository.getProjectHistory(projectId)
        if (history.isEmpty()) {
            throw Exception("No audit logs found for project ID: $projectId")
        }
        return history
    }

    fun getTaskAuditLogs(taskId: Int): List<AuditLogEntity> {
        val history = auditLogRepository.getTaskHistory(taskId)
        if (history.isEmpty()) {
            throw Exception("No audit logs found for task ID: $taskId")
        }
        return history
    }
}