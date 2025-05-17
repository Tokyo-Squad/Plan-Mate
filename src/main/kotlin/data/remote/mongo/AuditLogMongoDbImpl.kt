package org.example.data.remote.mongo


import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.example.data.RemoteDataSource
import org.example.data.remote.dto.AuditLogDto
import org.example.data.util.exception.DatabaseException
import org.example.data.util.exception.MongoExceptionHandler
import org.example.data.util.mapper.toAuditLogDto
import org.example.data.util.mapper.toDocument
import java.util.UUID

class AuditLogMongoDbImpl(
    mongoDBClient: MongoDBClient
) : RemoteDataSource<AuditLogDto> {
    private val auditLogCollection = mongoDBClient.getDatabase().getCollection<Document>("audit_log")

    override suspend fun add(item: AuditLogDto) {
        MongoExceptionHandler.handleOperation("adding audit log") {
            auditLogCollection.insertOne(item.toDocument())
        }
    }

    override suspend fun get(): List<AuditLogDto> {
        return MongoExceptionHandler.handleOperation("fetching all audit logs") {
            auditLogCollection.find().toList().map { it.toAuditLogDto() }
        }
    }

    override suspend fun getById(id: UUID): AuditLogDto? {
        return MongoExceptionHandler.handleOperation("fetching audit log by ID") {
            auditLogCollection.find(Filters.eq("id", id.toString())).firstOrNull()?.toAuditLogDto()
        }
    }

    override suspend fun update(item: AuditLogDto) {
        MongoExceptionHandler.handleOperation("updating audit log") {
            val filter = Filters.eq("id", item.id.toString())
            val result = auditLogCollection.replaceOne(filter, item.toDocument())
            if (result.matchedCount == 0L) {
                throw DatabaseException.DatabaseItemNotFoundException("Audit log with id ${item.id} not found")
            }
        }
    }

    override suspend fun delete(id: UUID) {
        MongoExceptionHandler.handleOperation("deleting audit log") {
            val result = auditLogCollection.deleteOne(Filters.eq("id", id.toString()))
            if (result.deletedCount == 0L) {
                throw DatabaseException.DatabaseItemNotFoundException("Audit log with id $id not found")
            }
        }
    }
}