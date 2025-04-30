package org.example.data

import org.example.entity.AuditLogEntity
import java.util.*

class AuditLogCsvImpl(
    fileName: String
) : DataProvider<AuditLogEntity> {
    override fun add(item: AuditLogEntity) {
        TODO("Not yet implemented")
    }

    override fun get(): List<AuditLogEntity> {
        TODO("Not yet implemented")
    }

    override fun getById(id: UUID): AuditLogEntity? {
        TODO("Not yet implemented")
    }

    override fun delete(id: UUID) {
        TODO("Not yet implemented")
    }

    override fun update(item: AuditLogEntity) {
        TODO("Not yet implemented")
    }
}