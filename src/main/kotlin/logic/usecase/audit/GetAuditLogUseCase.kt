package org.example.logic.usecase.audit

import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import org.example.utils.PlanMateException.InvalidStateIdException
import java.util.*

class GetAuditLogUseCase(
    private val auditLogRepository: AuditLogRepository
) {

    operator fun invoke(id: UUID, entityType: AuditedEntityType): List<AuditLogEntity> {
        return getAuditLogs(id,entityType)
    }

    private fun getAuditLogs(id: UUID, entityType: AuditedEntityType): List<AuditLogEntity> {
        return try {
            when (entityType) {
                AuditedEntityType.PROJECT -> (auditLogRepository.getProjectHistory(id))
                AuditedEntityType.TASK -> (auditLogRepository.getTaskHistory(id))
            }
        } catch (e: InvalidStateIdException) {
           return emptyList()
        }
    }
}