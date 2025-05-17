package org.example.data.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.example.data.RemoteDataSource
import org.example.data.remote.dto.UserDto
import org.example.data.util.exception.DatabaseException
import org.example.data.util.exception.MongoExceptionHandler
import org.example.data.util.mapper.toDocument
import org.example.data.util.mapper.toUserDto
import java.util.UUID

class UsersMongoImpl(
    mongoDBClient: MongoDBClient
) : RemoteDataSource<UserDto> {
    private val usersCollection = mongoDBClient.getDatabase().getCollection<Document>("users")

    override suspend fun add(item: UserDto) {
        MongoExceptionHandler.handleOperation("adding user") {
            usersCollection.insertOne(item.toDocument())
        }
    }

    override suspend fun get(): List<UserDto> = MongoExceptionHandler.handleOperation("fetching all users") {
        usersCollection.find()
            .map { it.toUserDto() }
            .toList()
    }


    override suspend fun getById(id: UUID): UserDto? {
        return MongoExceptionHandler.handleOperation("fetching user by ID") {
            val filter = Filters.eq("id", id.toString())
            val document = usersCollection.find(filter).firstOrNull()
            document?.toUserDto()
        }
    }

    override suspend fun update(item: UserDto) {
        MongoExceptionHandler.handleOperation("updating user") {
            val filter = Filters.eq("id", item.id.toString())
            val update = Updates.combine(
                Updates.set("username", item.username),
                Updates.set("password", item.password),
                Updates.set("type", item.type)
            )

            val result = usersCollection.updateOne(filter, update)

            if (result.matchedCount == 0L) {
                throw DatabaseException.DatabaseItemNotFoundException("User not found with id: ${item.id}")
            }
        }
    }

    override suspend fun delete(id: UUID) {
        MongoExceptionHandler.handleOperation("deleting user") {
            val filter = Filters.eq("id", id.toString())
            val result = usersCollection.deleteOne(filter)

            if (result.deletedCount == 0L) {
                throw DatabaseException.DatabaseItemNotFoundException("User not found with id: $id")
            }
        }
    }
}