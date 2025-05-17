package org.example.data.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.example.data.RemoteDataSource
import org.example.data.remote.dto.ProjectDto
import org.example.data.util.exception.DatabaseException
import org.example.data.util.exception.MongoExceptionHandler
import toDocument
import toProjectDto
import java.util.UUID

class ProjectMongoDBImpl(
    private val mongoClient: MongoDBClient
) : RemoteDataSource<ProjectDto> {

    private val collection: MongoCollection<Document> by lazy {
        mongoClient.getDatabase().getCollection("projects")
    }

    override suspend fun add(item: ProjectDto) {
        return MongoExceptionHandler.handleOperation("project creation") { collection.insertOne(item.toDocument()) }
    }

    override suspend fun get(): List<ProjectDto> = MongoExceptionHandler.handleOperation("projects retrieval") {
        collection.find()
            .map { it.toProjectDto() }
            .toList()
    }

    override suspend fun getById(id: UUID): ProjectDto? =
        MongoExceptionHandler.handleOperation("project retrieval by ID") {
            collection.find(Document("id", id))
                .firstOrNull()
                ?.toProjectDto()
        }

    override suspend fun update(item: ProjectDto) {
        MongoExceptionHandler.handleOperation("project update") {
            val result = collection.replaceOne(
                Document("id", item.id),
                item.toDocument()
            )

            if (result.modifiedCount == 0L) {
                throw DatabaseException.DatabaseItemNotFoundException("Project with ID ${item.id} not found")
            }
        }
    }

    override suspend fun delete(id: UUID) = MongoExceptionHandler.handleOperation("project deletion") {
        val result = collection.deleteOne(Document("id", id))

        if (result.deletedCount == 0L) {
            throw DatabaseException.DatabaseItemNotFoundException("Project with ID $id not found")
        }
    }
}