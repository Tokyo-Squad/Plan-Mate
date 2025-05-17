package org.example.data.remote.mongo


import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.LocalDateTime
import org.bson.Document
import org.example.data.DataProvider
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.utils.MongoExceptionHandler
import org.example.utils.PlanMateException
import java.util.*

class AuditLogMongoDbImpl(
    mongoDBClient: MongoDBClient
) : DataProvider<AuditLogEntity> {
    private val auditLogCollection = mongoDBClient.getDatabase().getCollection<Document>("audit_log")

    override suspend fun add(item: AuditLogEntity) {
        MongoExceptionHandler.handleOperation("adding audit log") {
            val document = toDocument(item)
            auditLogCollection.insertOne(document)
        }
    }

    override suspend fun get(): List<AuditLogEntity> {
        return MongoExceptionHandler.handleOperation("fetching all audit logs") {
            auditLogCollection.find().toList().map { fromDocument(it) }
        }
    }

    override suspend fun getById(id: UUID): AuditLogEntity? {
        return MongoExceptionHandler.handleOperation("fetching audit log by ID") {
            auditLogCollection.find(Filters.eq("_id", id.toString())).firstOrNull()?.let { fromDocument(it) }
        }
    }

    override suspend fun update(item: AuditLogEntity) {
        MongoExceptionHandler.handleOperation("updating audit log") {
            val filter = Filters.eq("id", item.id.toString())
            val document = toDocument(item)
            val result = auditLogCollection.replaceOne(filter, document)
            if (result.matchedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("Audit log with id ${item.id} not found")
            }
        }
    }

    override suspend fun delete(id: UUID) {
        MongoExceptionHandler.handleOperation("deleting audit log") {
            val result = auditLogCollection.deleteOne(Filters.eq("id", id.toString()))
            if (result.deletedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("Audit log with id $id not found")
            }
        }
    }

    private fun toDocument(auditLog: AuditLogEntity): Document {
        return Document()
            .append("id", auditLog.id)
            .append("userId", auditLog.userId.toString())
            .append("entityType", auditLog.entityType.toString())
            .append("entityId", auditLog.entityId.toString())
            .append("action", auditLog.action.toString())
            .append("changeDetails", auditLog.changeDetails)
            .append("timestamp", auditLog.timestamp.toString())
    }

    private fun fromDocument(document: Document): AuditLogEntity {
        return try {
            AuditLogEntity(
                id = document.get("id", UUID::class.java),
                userId = UUID.fromString(document.getString("userId")),
                entityType = AuditedEntityType.valueOf(document.getString("entityType")),
                entityId = UUID.fromString(document.getString("entityId")),
                action = AuditAction.valueOf(document.getString("action")),
                changeDetails = document.getString("changeDetails"),
                timestamp = document.getString("timestamp").let { LocalDateTime.parse(it) }
            )
        } catch (e: Exception) {
            throw PlanMateException.InvalidFormatException("Malformed audit log document: $document. ${e.message}")
        }
    }
}