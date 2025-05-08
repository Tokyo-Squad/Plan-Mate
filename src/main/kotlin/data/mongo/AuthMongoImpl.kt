package org.example.data.mongo

import kotlinx.coroutines.flow.first
import org.bson.Document
import org.example.data.AuthProvider
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.utils.MongoExceptionHandler
import org.example.utils.PlanMateException
import java.util.*

class AuthMongoImpl(
    private val mongoDBClient: MongoDBClient
) : AuthProvider {
    private val currentUserCollection = mongoDBClient.getDatabase().getCollection<Document>("current_users")

    override suspend fun addCurrentUser(user: UserEntity) {
        MongoExceptionHandler.handleOperation("adding current user") {
            // First delete any existing current user
            deleteCurrentUser()

            // Then add the new user
            val document = toDocument(user)
            currentUserCollection.insertOne(document)
        }
    }

    override suspend fun deleteCurrentUser() {
        MongoExceptionHandler.handleOperation("deleting current user") {
            currentUserCollection.deleteMany(Document())
        }
    }

    override suspend fun getCurrentUser(): UserEntity {
        return MongoExceptionHandler.handleOperation("fetching current user") {
            val document = currentUserCollection.find().first()
                ?: throw PlanMateException.ItemNotFoundException("No current user found.")

            fromDocument(document)
        }
    }

    private fun toDocument(user: UserEntity): Document {
        return Document()
            .append("_id", user.id.toString())
            .append("username", user.username)
            .append("password", user.password)
            .append("type", user.type.name)
    }

    private fun fromDocument(doc: Document): UserEntity {
        return try {
            UserEntity(
                id = UUID.fromString(doc.getString("_id")),
                username = doc.getString("username"),
                password = doc.getString("password"),
                type = UserType.valueOf(doc.getString("type"))
            )
        } catch (e: Exception) {
            throw PlanMateException.InvalidFormatException("Malformed MongoDB document: ${e.message}")
        }
    }
}