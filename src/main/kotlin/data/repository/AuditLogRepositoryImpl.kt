package org.example.data.repository

import org.example.data.RemoteDataSource
import org.example.data.remote.dto.AuditLogDto
import org.example.data.util.mapper.toAuditLogDto
import org.example.data.util.mapper.toAuditLogEntity
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import java.util.UUID

class AuditLogRepositoryImpl(
    private val remoteDataSource: RemoteDataSource<AuditLogDto>
) : AuditLogRepository {
    override suspend fun addAudit(auditLogEntity: AuditLogEntity) =
        remoteDataSource.add(auditLogEntity.toAuditLogDto())

    override suspend fun getProjectHistory(projectId: UUID): List<AuditLogEntity> =
        getEntityHistory(projectId, AuditedEntityType.PROJECT)

    override suspend fun getTaskHistory(taskId: UUID): List<AuditLogEntity> =
        getEntityHistory(taskId, AuditedEntityType.TASK)

    private suspend fun getEntityHistory(entityId: UUID, entityType: AuditedEntityType): List<AuditLogEntity> {
        val history = remoteDataSource.get().filter {
            AuditedEntityType.valueOf(it.entityType) == entityType && it.entityId == entityId
        }.map { it.toAuditLogEntity() }
        return history
    }
}
