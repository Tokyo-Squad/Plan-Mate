package org.example.data.repository

import org.example.data.RemoteDataSource
import org.example.data.remote.dto.AuditLogDto
import org.example.data.util.mapper.toAuditLogDto
import org.example.data.util.mapper.toAuditLogEntity
import domain.model.AuditLog
import domain.model.AuditedType
import org.example.logic.repository.AuditLogRepository
import java.util.UUID

class AuditLogRepositoryImpl(
    private val remoteDataSource: RemoteDataSource<AuditLogDto>
) : AuditLogRepository {
    override suspend fun addAudit(auditLog: AuditLog) =
        remoteDataSource.add(auditLog.toAuditLogDto())

    override suspend fun getProjectHistory(projectId: UUID): List<AuditLog> =
        getEntityHistory(projectId, AuditedType.PROJECT)

    override suspend fun getTaskHistory(taskId: UUID): List<AuditLog> =
        getEntityHistory(taskId, AuditedType.TASK)

    private suspend fun getEntityHistory(entityId: UUID, entityType: AuditedType): List<AuditLog> {
        val history = remoteDataSource.get().filter {
            AuditedType.valueOf(it.entityType) == entityType && it.entityId == entityId
        }.map { it.toAuditLogEntity() }
        return history
    }
}
