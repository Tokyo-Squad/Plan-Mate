package org.example.data.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.example.data.DataProvider
import org.example.entity.StateEntity
import org.example.utils.PlanMateException
import java.util.*

class StateMongoDBImpl(
    private val mongoClient: MongoDBClient
) : DataProvider<StateEntity> {

    private val collection: MongoCollection<Document> by lazy {
        mongoClient.getDatabase().getCollection("states")
    }

    override suspend fun add(item: StateEntity) {
        try {
            collection.insertOne(item.toDocument())
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("Error adding state: ${e.message}")
        }
    }

    override suspend fun get(): List<StateEntity> {
        return try {
            collection.find().toList().map { it.toStateEntity() }
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("Error reading states: ${e.message}")
        }
    }

    override suspend fun getById(id: UUID): StateEntity? {
        return try {
            collection.find(Document("_id", id)).firstOrNull()?.toStateEntity()
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("Error finding state: ${e.message}")
        }
    }

    override suspend fun update(item: StateEntity) {
        try {
            val result = collection.replaceOne(Document("_id", item.id), item.toDocument())
            if (result.modifiedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("State with ID ${item.id} not found.")
            }
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("Error updating state: ${e.message}")
        }
    }

    override suspend fun delete(id: UUID) {
        try {
            val result = collection.deleteOne(Document("_id", id))
            if (result.deletedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("State with ID $id not found.")
            }
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("Error deleting state: ${e.message}")
        }
    }
}

private fun StateEntity.toDocument(): Document {
    return Document().apply {
        append("_id", this@toDocument.id)
        append("name", this@toDocument.name)
    }
}

private fun Document.toStateEntity(): StateEntity {
    return StateEntity(
        id = this["_id"] as UUID, name = this.getString("name"), projectId = this["projectId"] as UUID
    )
}