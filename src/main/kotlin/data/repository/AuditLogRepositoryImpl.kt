package org.example.data.repository

import org.example.data.DataProvider
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import org.example.utils.PlanMateException
import java.util.*

class AuditLogRepositoryImpl(
    private val dataProvider: DataProvider<AuditLogEntity>
) : AuditLogRepository {
    override fun addAudit(auditLogEntity: AuditLogEntity) {
        try {
            dataProvider.add(auditLogEntity)
        }catch (e: PlanMateException.FileWriteException) {
            throw PlanMateException.FileWriteException("Failed to add audit log due to invalid state.")
        }
    }

    override fun getProjectHistory(projectId: UUID): List<AuditLogEntity> =
        getEntityHistory(projectId, AuditedEntityType.PROJECT)


    override fun getTaskHistory(taskId: UUID): List<AuditLogEntity> =
        getEntityHistory(taskId, AuditedEntityType.TASK)


    private fun getEntityHistory(entityId: UUID, entityType: AuditedEntityType): List<AuditLogEntity> {
        val history = dataProvider.get().filter {
            it.entityType == entityType && it.entityId == entityId
        }
        return history
    }
}
