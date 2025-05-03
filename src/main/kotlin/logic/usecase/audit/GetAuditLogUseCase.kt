package org.example.logic.usecase.audit

import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import org.example.utils.PlanMateException.InvalidStateIdException
import java.util.*

class GetAuditLogUseCase(
    private val auditLogRepository: AuditLogRepository
) {

    operator fun invoke(id: UUID, entityType: AuditedEntityType): Result<List<AuditLogEntity>> {
        return getAuditLogs(id,entityType)
    }

    private fun getAuditLogs(id: UUID, entityType: AuditedEntityType): Result<List<AuditLogEntity>> {
        return try {
            when (entityType) {
                AuditedEntityType.PROJECT -> Result.success(auditLogRepository.getProjectHistory(id))
                AuditedEntityType.TASK -> Result.success(auditLogRepository.getTaskHistory(id))
            }
        } catch (e: InvalidStateIdException) {
            Result.failure(e)
        }
    }
}