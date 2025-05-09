package org.example.data.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.LocalDateTime
import org.bson.Document
import org.example.data.DataProvider
import org.example.entity.ProjectEntity
import org.example.utils.MongoExceptionHandler
import org.example.utils.PlanMateException
import java.util.*

class ProjectMongoDBImpl(
    private val mongoClient: MongoDBClient
) : DataProvider<ProjectEntity> {

    private val collection: MongoCollection<Document> by lazy {
        mongoClient.getDatabase().getCollection("projects")
    }

    override suspend fun add(item: ProjectEntity) {
        return MongoExceptionHandler.handleOperation("project creation") { collection.insertOne(item.toDocument()) }
    }

    override suspend fun get(): List<ProjectEntity> = MongoExceptionHandler.handleOperation("projects retrieval") {
        collection.find()
            .map { it.toProjectEntity() }
            .toList()
    }

    override suspend fun getById(id: UUID): ProjectEntity? =
        MongoExceptionHandler.handleOperation("project retrieval by ID") {
            collection.find(Document("id", id))
                .firstOrNull()
                ?.toProjectEntity()
        }

    override suspend fun update(item: ProjectEntity) {
        MongoExceptionHandler.handleOperation("project update") {
            val result = collection.replaceOne(
                Document("id", item.id),
                item.toDocument()
            )

            if (result.modifiedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("Project with ID ${item.id} not found")
            }
        }
    }

    override suspend fun delete(id: UUID) = MongoExceptionHandler.handleOperation("project deletion") {
        val result = collection.deleteOne(Document("id", id))

        if (result.deletedCount == 0L) {
            throw PlanMateException.ItemNotFoundException("Project with ID $id not found")
        }
    }

    private fun ProjectEntity.toDocument(): Document {
        return Document().apply {
            put("id", id)
            put("name", name)
            put("createdByAdminId", createdByAdminId)
            put("createdAt", createdAt.toString())
        }
    }

    private fun Document.toProjectEntity(): ProjectEntity {
        return ProjectEntity(
            id = get("id", UUID::class.java),
            name = getString("name"),
            createdByAdminId = this.get("createdByAdminId", UUID::class.java),
            createdAt = LocalDateTime.parse(getString("createdAt"))
        )
    }
}