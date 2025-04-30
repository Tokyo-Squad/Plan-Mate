package org.example.data.repository

import org.example.data.DataProvider
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import java.util.UUID

class AuditLogRepositoryImpl(
    private val dataProvider: DataProvider<AuditLogEntity>
) : AuditLogRepository {
    override fun addAudit(auditLogEntity: AuditLogEntity) {
        dataProvider.add(auditLogEntity)
    }

    override fun getProjectHistory(projectId: Int): List<AuditLogEntity> =
        getEntityHistory(projectId, AuditedEntityType.PROJECT)


    override fun getTaskHistory(taskId: Int): List<AuditLogEntity> =
        getEntityHistory(taskId, AuditedEntityType.TASK)


    private fun getEntityHistory(entityId: Int, entityType: AuditedEntityType): List<AuditLogEntity> {
        val uuid = UUID.nameUUIDFromBytes(entityId.toString().toByteArray())
        val history = dataProvider.get().filter { it.entityType == entityType && it.entityId == uuid }
        return history
    }
}
