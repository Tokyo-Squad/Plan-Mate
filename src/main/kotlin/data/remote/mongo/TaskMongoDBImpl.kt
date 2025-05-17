package org.example.data.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.example.data.RemoteDataSource
import org.example.data.remote.dto.TaskDto
import org.example.data.util.exception.DatabaseException
import org.example.data.util.exception.MongoExceptionHandler
import org.example.data.util.mapper.toDocument
import org.example.data.util.mapper.toTaskDto
import java.util.UUID

class TaskMongoDBImpl(private val mongoClient: MongoDBClient) : RemoteDataSource<TaskDto> {
    private val collection: MongoCollection<Document> by lazy {
        mongoClient.getDatabase().getCollection("tasks")
    }

    override suspend fun add(item: TaskDto) {
        MongoExceptionHandler.handleOperation("adding task") {
            collection.insertOne(item.toDocument())
        }
    }

    override suspend fun get(): List<TaskDto> = MongoExceptionHandler.handleOperation("fetching all tasks") {
        collection.find()
            .toList()
            .map { it.toTaskDto() }
    }

    override suspend fun getById(id: UUID): TaskDto? = MongoExceptionHandler.handleOperation("fetching task by ID") {
        collection.find(Document("id", id))
            .firstOrNull()
            ?.toTaskDto()
    }

    override suspend fun update(item: TaskDto) {
        MongoExceptionHandler.handleOperation("updating task") {
            val result = collection.replaceOne(Document("id", item.id), item.toDocument())
            if (result.modifiedCount == 0L) {
                throw DatabaseException.DatabaseItemNotFoundException("Task with ID ${item.id} not found.")
            }
        }
    }

    override suspend fun delete(id: UUID) {
        MongoExceptionHandler.handleOperation("deleting task") {
            val result = collection.deleteOne(Document("id", id))
            if (result.deletedCount == 0L) {
                throw DatabaseException.DatabaseItemNotFoundException("Task with ID $id not found.")
            }
        }
    }
}