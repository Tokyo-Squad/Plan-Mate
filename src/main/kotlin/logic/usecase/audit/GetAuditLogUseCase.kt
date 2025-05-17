package org.example.logic.usecase.audit

import logic.model.AuditLog
import logic.model.AuditedType
import org.example.logic.repository.AuditLogRepository
import java.util.UUID

class GetAuditLogUseCase(
    private val auditLogRepository: AuditLogRepository
) {
    suspend operator fun invoke(id: UUID, entityType: AuditedType): List<AuditLog> = when (entityType) {
        AuditedType.PROJECT -> (auditLogRepository.getProjectHistory(id))
        AuditedType.TASK -> (auditLogRepository.getTaskHistory(id))
    }
}