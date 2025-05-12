package org.example.logic.usecase.audit

import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import java.util.UUID

class GetAuditLogUseCase(
    private val auditLogRepository: AuditLogRepository
) {
    suspend operator fun invoke(id: UUID, entityType: AuditedEntityType): List<AuditLogEntity> = when (entityType) {
        AuditedEntityType.PROJECT -> (auditLogRepository.getProjectHistory(id))
        AuditedEntityType.TASK -> (auditLogRepository.getTaskHistory(id))
    }
}