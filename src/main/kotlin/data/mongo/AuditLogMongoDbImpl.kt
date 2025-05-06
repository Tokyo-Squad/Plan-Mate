package org.example.data.mongo

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.toList
import org.example.data.DataProvider
import org.example.entity.AuditLogEntity
import java.util.UUID
import java.util.*
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull


class AuditLogMongoDbImpl(
    private val mongoClient: MongoClient
): DataProvider<AuditLogEntity>  {
    private val auditLogCollection: MongoCollection<AuditLogEntity> = mongoClient.getDatabase().getCollection("audit-logs")
    override suspend fun add(item: AuditLogEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun get(): List<AuditLogEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: UUID): AuditLogEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun update(item: AuditLogEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: UUID) {
        TODO("Not yet implemented")
    }
}