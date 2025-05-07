package org.example.data.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.example.data.DataProvider
import org.example.data.utils.toDocument
import org.example.data.utils.toProjectEntity
import org.example.entity.ProjectEntity
import org.example.utils.PlanMateException
import java.util.*

class ProjectMongoDBImpl(
    private val mongoClient: MongoDBClient
) : DataProvider<ProjectEntity> {

    private val collection: MongoCollection<Document> by lazy {
        mongoClient.getDatabase().getCollection("projects")
    }

    override suspend fun add(item: ProjectEntity) {
        try {
            collection.insertOne(item.toDocument())
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("Error adding project: ${e.message}")
        }

    }

    override suspend fun get(): List<ProjectEntity> {
        return try {
            collection.find()
                .map { it.toProjectEntity() }
                .toList()
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("Error reading projects: ${e.message}")
        }
    }

    override suspend fun getById(id: UUID): ProjectEntity? {
        return try {
            collection.find(Document("_id", id)).firstOrNull()?.toProjectEntity()
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("Error finding project: ${e.message}")
        }
    }

    override suspend fun update(item: ProjectEntity) {
        try {
            val result = collection.replaceOne(
                Document("_id", item.id),
                item.toDocument()
            )

            if (result.modifiedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("Project with ID ${item.id} not found.")
            }
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("Error updating project: ${e.message}")
        }
    }

    override suspend fun delete(id: UUID) {
        try {
            val result = collection.deleteOne(Document("_id", id))

            if (result.deletedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("Project with ID $id not found.")
            }
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("Error deleting project: ${e.message}")
        }
    }
}