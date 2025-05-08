package org.example.data.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.example.data.DataProvider
import org.example.entity.StateEntity
import org.example.utils.MongoExceptionHandler
import org.example.utils.PlanMateException
import java.util.*

class StateMongoDBImpl(
    mongoClient: MongoDBClient
) : DataProvider<StateEntity> {

    private val collection = mongoClient.getDatabase().getCollection<Document>("states")

    override suspend fun add(item: StateEntity) {
        MongoExceptionHandler.handleOperation("adding state") {
            collection.insertOne(toDocument(item))
        }
    }

    override suspend fun get(): List<StateEntity> {
        return MongoExceptionHandler.handleOperation("fetching all states") {
            collection.find()
                .map { fromDocument(it) }
                .toList()
        }
    }

    override suspend fun getById(id: UUID): StateEntity? {
        return MongoExceptionHandler.handleOperation("fetching state by ID") {
            val filter = Filters.eq("_id", id.toString())
            val document = collection.find(filter).firstOrNull()
            document?.let { fromDocument(it) }
                ?: throw PlanMateException.ItemNotFoundException("State not found with ID: $id")
        }
    }

    override suspend fun update(item: StateEntity) {
        MongoExceptionHandler.handleOperation("updating state") {
            val filter = Filters.eq("_id", item.id.toString())
            val update = Updates.combine(
                Updates.set("name", item.name),
                Updates.set("projectId", item.projectId.toString())
            )
            val result = collection.updateOne(filter, update)
            if (result.matchedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("State not found with ID: ${item.id}")
            }
        }
    }

    override suspend fun delete(id: UUID) {
        MongoExceptionHandler.handleOperation("deleting state") {
            val filter = Filters.eq("_id", id.toString())
            val result = collection.deleteOne(filter)
            if (result.deletedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("State not found with ID: $id")
            }
        }
    }

    private fun toDocument(item: StateEntity): Document {
        return Document()
            .append("id", item.id)
            .append("name", item.name)
            .append("projectId", item.projectId.toString())
    }

    private fun fromDocument(doc: Document): StateEntity {
        return try {
            StateEntity(
                id = doc.get("id", UUID::class.java),
                name = doc.getString("name"),
                projectId = UUID.fromString(doc.getString("projectId"))
            )
        } catch (e: Exception) {
            throw PlanMateException.InvalidFormatException("Malformed MongoDB document: ${e.message}")
        }
    }
}