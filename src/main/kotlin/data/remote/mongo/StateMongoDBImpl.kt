package org.example.data.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.example.data.RemoteDataSource
import org.example.data.remote.dto.WorkflowStateDto
import org.example.data.util.exception.DatabaseException
import org.example.data.util.exception.MongoExceptionHandler
import org.example.data.util.mapper.toDocument
import org.example.data.util.mapper.toStateDto
import java.util.UUID

class StateMongoDBImpl(
    mongoClient: MongoDBClient
) : RemoteDataSource<WorkflowStateDto> {

    private val collection = mongoClient.getDatabase().getCollection<Document>("states")

    override suspend fun add(item: WorkflowStateDto) {
        MongoExceptionHandler.handleOperation("adding state") {
            collection.insertOne(item.toDocument())
        }
    }

    override suspend fun get(): List<WorkflowStateDto> = MongoExceptionHandler.handleOperation("fetching all states") {
        collection.find().map { it.toStateDto() }.toList()
    }

    override suspend fun getById(id: UUID): WorkflowStateDto?=
        MongoExceptionHandler.handleOperation("fetching state by ID") {
            val filter = Filters.eq("id", id.toString())
            val document = collection.find(filter).firstOrNull()
            document?.toStateDto()
        }


    override suspend fun update(item: WorkflowStateDto) {
        MongoExceptionHandler.handleOperation("updating state") {
            val filter = Filters.eq("id", item.id.toString())
            val update = Updates.combine(
                Updates.set("name", item.name),
                Updates.set("projectId", item.projectId.toString())
            )
            val result = collection.updateOne(filter, update)
            if (result.matchedCount == 0L) {
                throw DatabaseException.DatabaseItemNotFoundException("State not found with ID: ${item.id}")
            }
        }
    }

    override suspend fun delete(id: UUID) {
        MongoExceptionHandler.handleOperation("deleting state") {
            val filter = Filters.eq("id", id.toString())
            val result = collection.deleteOne(filter)
            if (result.deletedCount == 0L) {
                throw DatabaseException.DatabaseItemNotFoundException("State not found with ID: $id")
            }
        }
    }
}