package org.example.data.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.toLocalDateTime
import org.bson.Document
import org.example.data.RemoteDataSource
import org.example.entity.TaskEntity
import org.example.utils.MongoExceptionHandler
import org.example.utils.PlanMateException
import java.util.*

class TaskMongoDBImpl(private val mongoClient: MongoDBClient) : RemoteDataSource<TaskEntity> {
    private val collection: MongoCollection<Document> by lazy {
        mongoClient.getDatabase().getCollection("tasks")
    }

    override suspend fun add(item: TaskEntity) {
        MongoExceptionHandler.handleOperation("adding task") {
            collection.insertOne(item.toDocument())
        }
    }

    override suspend fun get(): List<TaskEntity> = MongoExceptionHandler.handleOperation("fetching all tasks") {
        collection.find()
            .toList()
            .map { it.toTaskEntity() }
    }

    override suspend fun getById(id: UUID): TaskEntity? = MongoExceptionHandler.handleOperation("fetching task by ID") {
        collection.find(Document("id", id))
            .firstOrNull()
            ?.toTaskEntity()
    }

    override suspend fun update(item: TaskEntity) {
        MongoExceptionHandler.handleOperation("updating task") {
            val result = collection.replaceOne(Document("id", item.id), item.toDocument())
            if (result.modifiedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("Task with ID ${item.id} not found.")
            }
        }
    }

    override suspend fun delete(id: UUID) {
        MongoExceptionHandler.handleOperation("deleting task") {
            val result = collection.deleteOne(Document("id", id))
            if (result.deletedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("Task with ID $id not found.")
            }
        }
    }


    private fun TaskEntity.toDocument() = Document().apply {
        append("id", id)
        append("title", title)
        append("description", description)
        append("stateId", stateId)
        append("projectId", projectId)
        append("createdByUserId", createdByUserId)
        append("createdAt", createdAt.toString())
    }

    private fun Document.toTaskEntity() = TaskEntity(
        id = get("id", UUID::class.java),
        title = getString("title"),
        description = getString("description"),
        stateId = get("stateId", UUID::class.java),
        projectId = get("projectId", UUID::class.java),
        createdByUserId = get("createdByUserId", UUID::class.java),
        createdAt = getString("createdAt").toLocalDateTime()
    )
}