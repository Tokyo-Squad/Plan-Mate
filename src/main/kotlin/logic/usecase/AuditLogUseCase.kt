package org.example.logic.usecase

import org.example.entity.AuditLogEntity
import org.example.logic.repository.AuditLogRepository

class AuditLogUseCase(
    private val auditLogRepository: AuditLogRepository
) {

    fun addAuditLog(auditLogEntity: AuditLogEntity) {
        return auditLogRepository.addAudit(auditLogEntity)
    }

    fun getProjectAuditLogs(projectId: Int): Result<List<AuditLogEntity>> {
        return try {
            Result.success(auditLogRepository.getProjectHistory(projectId))
        }catch (e: NoSuchElementException){
                Result.failure(e)
        }
    }

    fun getTaskAuditLogs(taskId: Int): Result<List<AuditLogEntity>> {
        return try {
            Result.success(auditLogRepository.getTaskHistory(taskId))
        }catch (e: NoSuchElementException){
            Result.failure(e)
        }
    }
}