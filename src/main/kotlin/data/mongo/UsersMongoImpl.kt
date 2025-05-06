package org.example.data.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.DataProvider
import org.example.entity.UserEntity
import org.example.utils.MongoExceptionHandler
import org.example.utils.PlanMateException
import java.util.*

class UsersMongoImpl(
    private val db: MongoDBClient
) : DataProvider<UserEntity> {
    private val usersCollection: MongoCollection<UserEntity> = db.getDatabase().getCollection("users")

    override fun add(item: UserEntity) {
        MongoExceptionHandler.handleOperation("user creation") {
            usersCollection.insertOne(item)
        }
    }

    override fun get(): List<UserEntity> {
        return MongoExceptionHandler.handleOperation("fetching all users") {
            usersCollection.find().toList()
        }
    }

    override fun getById(id: UUID): UserEntity? {
        return MongoExceptionHandler.handleOperation("fetching user by ID") {
            usersCollection.find(Filters.eq("_id", id))
                .firstOrNull()
                ?: throw PlanMateException.ItemNotFoundException("User not found with id: $id")
        }
    }

    override fun update(item: UserEntity) {
        MongoExceptionHandler.handleOperation("updating user") {
            val filter = Filters.eq("_id", item.id)
            val options = ReplaceOptions().upsert(false)
            val result = usersCollection.replaceOne(filter, item, options)

            if (result.matchedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("User not found with id: ${item.id}")
            }
        }
    }

    override fun delete(id: UUID) {
        MongoExceptionHandler.handleOperation("deleting user") {
            val result = usersCollection.deleteOne(Filters.eq("_id", id))

            if (result.deletedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("User not found with id: $id")
            }
        }
    }
}