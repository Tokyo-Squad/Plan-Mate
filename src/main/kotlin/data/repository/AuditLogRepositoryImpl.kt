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

    override fun getProjectHistory(projectId: Int): List<AuditLogEntity> {
        val projectUUID = UUID.nameUUIDFromBytes(projectId.toString().toByteArray())
        val history = dataProvider.get().filter { it.entityType == AuditedEntityType.PROJECT && it.entityId == projectUUID }
        if (history.isEmpty()) {
            throw NoSuchElementException("No audit logs found for project ID: $projectId")
        }
        return history
    }

    override fun getTaskHistory(taskId: Int): List<AuditLogEntity> {
        TODO("Not yet implemented")
    }

}
