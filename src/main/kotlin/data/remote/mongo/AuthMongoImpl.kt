package org.example.data.remote.mongo

import kotlinx.coroutines.flow.first
import org.bson.Document
import org.example.data.AuthProvider
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.data.util.exception.MongoExceptionHandler
import org.example.utils.PlanMateException
import java.util.*

class AuthMongoImpl(
    mongoDBClient: MongoDBClient
) : AuthProvider {
    private val currentUserCollection = mongoDBClient.getDatabase().getCollection<Document>("current_users")

    override suspend fun addCurrentUser(user: UserEntity) {
        MongoExceptionHandler.handleOperation("adding current user") {
            deleteCurrentUser()

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
            fromDocument(document)
        }
    }

    private fun toDocument(user: UserEntity): Document {
        return Document()
            .append("id", user.id)
            .append("username", user.username)
            .append("password", user.password)
            .append("type", user.type.name)
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