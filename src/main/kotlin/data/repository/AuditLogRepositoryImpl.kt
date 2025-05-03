package org.example.data.repository

import org.example.data.DataProvider
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import org.example.utils.PlanMateException
import org.example.utils.PlanMateException.InvalidStateIdException
import java.util.UUID

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

    override fun getProjectHistory(projectId: Int): List<AuditLogEntity> =
        getEntityHistory(projectId, AuditedEntityType.PROJECT)


    override fun getTaskHistory(taskId: Int): List<AuditLogEntity> =
        getEntityHistory(taskId, AuditedEntityType.TASK)


    private fun getEntityHistory(entityId: Int, entityType: AuditedEntityType): List<AuditLogEntity> {
        val uuid = UUID.nameUUIDFromBytes(entityId.toString().toByteArray())
        val history = dataProvider.get().filter { it.entityType == entityType && it.entityId == uuid }
        if (history.isEmpty()) {
            throw InvalidStateIdException()
        }
        return history
    }
}
