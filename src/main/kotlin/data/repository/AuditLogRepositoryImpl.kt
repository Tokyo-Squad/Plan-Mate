package org.example.data.repository

import org.example.data.DataProvider
import org.example.entity.AuditLogEntity
import org.example.logic.repository.AuditLogRepository

class AuditLogRepositoryImpl(
    private val dataProvider: DataProvider<AuditLogEntity>
) : AuditLogRepository {
    override fun addAudit(auditLogEntity: AuditLogEntity) {
        TODO("Not yet implemented")
    }

    override fun getProjectHistory(projectId: Int): List<AuditLogEntity> {
        TODO("Not yet implemented")
    }

    override fun getTaskHistory(taskId: Int): List<AuditLogEntity> {
        TODO("Not yet implemented")
    }

    override fun ensureAuditCsvExists(filePath: String): Boolean {
        TODO("Not yet implemented")
    }
}
