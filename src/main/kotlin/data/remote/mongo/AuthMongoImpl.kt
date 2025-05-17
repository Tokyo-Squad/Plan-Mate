package org.example.data.remote.mongo

import kotlinx.coroutines.flow.first
import org.bson.Document
import org.example.data.Authentication
import org.example.data.remote.dto.UserDto
import org.example.data.util.exception.MongoExceptionHandler
import org.example.data.util.mapper.toDocument
import org.example.data.util.mapper.toUserDto

class AuthMongoImpl(
    mongoDBClient: MongoDBClient
) : Authentication {
    private val currentUserCollection = mongoDBClient.getDatabase().getCollection<Document>("current_users")

    override suspend fun addCurrentUser(user: UserDto) {
        MongoExceptionHandler.handleOperation("adding current user") {
            deleteCurrentUser()
            currentUserCollection.insertOne(user.toDocument())
        }
    }

    override suspend fun deleteCurrentUser() {
        MongoExceptionHandler.handleOperation("deleting current user") {
            currentUserCollection.deleteMany(Document())
        }
    }

    override suspend fun getCurrentUser(): UserDto {
        return MongoExceptionHandler.handleOperation("fetching current user") {
            currentUserCollection.find().first().toUserDto()
        }
    }
}