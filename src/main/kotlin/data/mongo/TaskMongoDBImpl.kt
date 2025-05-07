package org.example.data.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.example.data.DataProvider
import org.example.entity.TaskEntity
import org.example.utils.PlanMateException
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

class TaskMongoDBImpl(private val mongoClient: MongoDBClient) : DataProvider<TaskEntity> {
    private val collection: MongoCollection<Document> by lazy {
        mongoClient.getDatabase().getCollection("tasks")
    }

    override suspend fun add(item: TaskEntity) {
        execute { collection.insertOne(item.toDocument()) }
    }

    override suspend fun get(): List<TaskEntity> = execute {
        collection.find().toList().map { it.toTaskEntity() }
    }

    override suspend fun getById(id: UUID): TaskEntity? = execute {
        collection.find(Document("_id", id)).firstOrNull()?.toTaskEntity()
    }

    override suspend fun update(item: TaskEntity) {
        execute {
            val result = collection.replaceOne(Document("_id", item.id), item.toDocument())
            if (result.modifiedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("Task with ID ${item.id} not found.")
            }
        }
    }

    override suspend fun delete(id: UUID) {
        execute {
            val result = collection.deleteOne(Document("_id", id))
            if (result.deletedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("Task with ID $id not found.")
            }
        }
    }

    private suspend fun <T> execute(action: suspend () -> T): T = try {
        action()
    } catch (e: PlanMateException) {
        throw e
    } catch (e: Exception) {
        throw PlanMateException.DatabaseException("${e.message}")
    }
}

private fun TaskEntity.toDocument() = Document().apply {
    append("_id", id)
    append("title", title)
    append("description", description)
    append("stateId", stateId)
    append("projectId", projectId)
    append("createdByUserId", createdByUserId)
    append("createdAt", createdAt.toString())
}

private fun Document.toTaskEntity() = TaskEntity(
    id = get("_id", UUID::class.java),
    title = getString("title"),
    description = getString("description"),
    stateId = get("stateId", UUID::class.java),
    projectId = get("projectId", UUID::class.java),
    createdByUserId = get("createdByUserId", UUID::class.java),
    createdAt = getString("createdAt").toLocalDateTime()
)