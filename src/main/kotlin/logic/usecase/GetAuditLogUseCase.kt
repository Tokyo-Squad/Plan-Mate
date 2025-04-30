package org.example.logic.usecase

import org.example.entity.AuditLogEntity
import org.example.logic.repository.AuditLogRepository

class GetAuditLogUseCase(
    private val auditLogRepository: AuditLogRepository
) {

    operator fun invoke(id: Int): Result<List<AuditLogEntity>> {
        return getAuditLogs(id)
    }

    private fun getAuditLogs(id: Int): Result<List<AuditLogEntity>> {
        return try {
            Result.success(auditLogRepository.getTaskHistory(id))
        } catch (e: NoSuchElementException) {
            Result.failure(e)
        }
    }
}