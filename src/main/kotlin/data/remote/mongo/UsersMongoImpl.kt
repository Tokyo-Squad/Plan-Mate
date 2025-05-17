package org.example.data.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.example.data.RemoteDataSource
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.utils.MongoExceptionHandler
import org.example.utils.PlanMateException
import java.util.*

class UsersMongoImpl(
    mongoDBClient: MongoDBClient
) : RemoteDataSource<UserEntity> {
    private val usersCollection = mongoDBClient.getDatabase().getCollection<Document>("users")

    override suspend fun add(item: UserEntity) {
        MongoExceptionHandler.handleOperation("adding user") {
            val document = toDocument(item)
            usersCollection.insertOne(document)
        }
    }

    override suspend fun get(): List<UserEntity> {
        return MongoExceptionHandler.handleOperation("fetching all users") {
            usersCollection.find()
                .map { doc -> fromDocument(doc) }
                .toList()
        }
    }

    override suspend fun getById(id: UUID): UserEntity {
        return MongoExceptionHandler.handleOperation("fetching user by ID") {
            val filter = Filters.eq("id", id.toString())
            val document = usersCollection.find(filter).firstOrNull()
            document?.let { fromDocument(it) }
                ?: throw PlanMateException.ItemNotFoundException("User not found with id: $id")
        }
    }

    override suspend fun update(item: UserEntity) {
        MongoExceptionHandler.handleOperation("updating user") {
            val filter = Filters.eq("id", item.id.toString())
            val update = Updates.combine(
                Updates.set("username", item.username),
                Updates.set("password", item.password),
                Updates.set("type", item.type.name)
            )

            val result = usersCollection.updateOne(filter, update)

            if (result.matchedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("User not found with id: ${item.id}")
            }
        }
    }

    override suspend fun delete(id: UUID) {
        MongoExceptionHandler.handleOperation("deleting user") {
            val filter = Filters.eq("id", id.toString())
            val result = usersCollection.deleteOne(filter)

            if (result.deletedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("User not found with id: $id")
            }
        }
    }

    private fun toDocument(item: UserEntity): Document {
        return Document()
            .append("id", item.id)
            .append("username", item.username)
            .append("password", item.password)
            .append("type", item.type.name)
    }

    private fun fromDocument(doc: Document): UserEntity {
        return try {
            UserEntity(
                id = doc.get("id", UUID::class.java),
                username = doc.getString("username"),
                password = doc.getString("password"),
                type = UserType.valueOf(doc.getString("type"))
            )
        } catch (e: Exception) {
            throw PlanMateException.InvalidFormatException("Malformed MongoDB document: ${e.message}")
        }
    }
}