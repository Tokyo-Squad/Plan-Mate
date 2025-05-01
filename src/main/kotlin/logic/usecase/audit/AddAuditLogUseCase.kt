package org.example.logic.usecase.audit

import org.example.entity.AuditLogEntity
import org.example.logic.repository.AuditLogRepository

class AddAuditLogUseCase(
    private val auditLogRepository: AuditLogRepository
) {
    operator fun invoke(auditLogEntity: AuditLogEntity) {
        auditLogRepository.addAudit(auditLogEntity)
    }
}