package org.example.logic.usecase.audit

import org.example.entity.AuditLogEntity
import org.example.logic.repository.AuditLogRepository
import org.example.utils.PlanMatException.InvalidStateIdException

class AddAuditLogUseCase(
    private val auditLogRepository: AuditLogRepository
) {
    operator fun invoke(auditLogEntity: AuditLogEntity): Result<Boolean> {
        return try {
            auditLogRepository.addAudit(auditLogEntity)
            Result.success(true)
        } catch (e: InvalidStateIdException) {
            Result.failure(e)
        }
    }
}