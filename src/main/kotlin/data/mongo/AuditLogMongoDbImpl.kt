package org.example.data.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.example.data.DataProvider
import org.example.entity.AuditLogEntity
import org.example.utils.MongoExceptionHandler
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import java.util.UUID
import org.example.utils.PlanMateException

class AuditLogMongoDbImpl(
    private val database: MongoDatabase
) : DataProvider<AuditLogEntity> {

    private val collection: MongoCollection<AuditLogEntity> =
        database.getCollection("audit-log", AuditLogEntity::class.java)

    override suspend fun add(item: AuditLogEntity) {
        MongoExceptionHandler.handleOperation("adding audit log") {
            collection.insertOne(item)
        }
    }

    override suspend fun get(): List<AuditLogEntity> {
        return MongoExceptionHandler.handleOperation("fetching all audit logs") {
            collection.find().toList()
        }
    }

    override suspend fun getById(id: UUID): AuditLogEntity? {
        return MongoExceptionHandler.handleOperation("fetching audit log by ID") {
            collection.find(Filters.eq("_id", id)).firstOrNull()
        }
    }

    override suspend fun update(item: AuditLogEntity) {
        MongoExceptionHandler.handleOperation("updating audit log") {
            val filter = Filters.eq("_id", item.id)
            val result = collection.replaceOne(filter, item)
            if (result.matchedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("Audit log with id ${item.id} not found")
            }
        }
    }

    override suspend fun delete(id: UUID) {
        MongoExceptionHandler.handleOperation("deleting audit log") {
            val result = collection.deleteOne(Filters.eq("_id", id))
            if (result.deletedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("Audit log with id $id not found")
            }
        }
    }
}