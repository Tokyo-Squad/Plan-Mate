package org.example.logic.usecase.audit

import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository

class GetAuditLogUseCase(
    private val auditLogRepository: AuditLogRepository
) {

    operator fun invoke(id: Int, entityType: AuditedEntityType): Result<List<AuditLogEntity>> {
        return getAuditLogs(id,entityType)
    }

    private fun getAuditLogs(id: Int, entityType: AuditedEntityType): Result<List<AuditLogEntity>> {
        return try {
            when (entityType) {
                AuditedEntityType.PROJECT -> Result.success(auditLogRepository.getProjectHistory(id))
                AuditedEntityType.TASK -> Result.success(auditLogRepository.getTaskHistory(id))
            }
        } catch (e: NoSuchElementException) {
            Result.failure(e)
        }
    }
}