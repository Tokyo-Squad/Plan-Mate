package org.example.data.mongo

import com.mongodb.MongoException
import com.mongodb.MongoSecurityException
import com.mongodb.MongoTimeoutException
import com.mongodb.MongoWriteException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.DataProvider
import org.example.entity.UserEntity
import org.example.utils.PlanMateException
import java.util.*

class UsersMongoImpl(
    private val db: MongoDBClient
) : DataProvider<UserEntity> {
    private val usersCollection = db.getDatabase().getCollection<UserEntity>("users")

    private fun handleMongoException(e: Exception, operation: String): Nothing {
        throw when (e) {
            is MongoWriteException -> {
                when (e.error.code) {
                    11000 -> PlanMateException.DuplicateKeyException(
                        "Duplicate key error during $operation: ${e.message}"
                    )

                    else -> PlanMateException.DatabaseOperationException(
                        "Write error during $operation: ${e.message}"
                    )
                }
            }

            is MongoTimeoutException -> PlanMateException.DatabaseTimeoutException(
                "Operation timeout during $operation: ${e.message}"
            )

            is MongoSecurityException -> PlanMateException.DatabaseAuthenticationException(
                "Authentication failed during $operation: ${e.message}"
            )

            is MongoException -> PlanMateException.DatabaseOperationException(
                "Database error during $operation: ${e.message}"
            )

            else -> PlanMateException.UnknownException(
                "Unexpected error during $operation: ${e.message}"
            )
        }
    }

    override fun add(item: UserEntity) {
        try {
            usersCollection.insertOne(item)
        } catch (e: Exception) {
            handleMongoException(e, "user creation")
        }
    }

    override fun get(): List<UserEntity> {
        try {
            return usersCollection.find().toList()
        } catch (e: Exception) {
            handleMongoException(e, "fetching all users")
        }
    }

    override fun getById(id: UUID): UserEntity? {
        try {
            return usersCollection.find(Filters.eq("_id", id))
                .firstOrNull()
                ?: throw PlanMateException.ItemNotFoundException("User not found with id: $id")
        } catch (e: PlanMateException) {
            throw e
        } catch (e: Exception) {
            handleMongoException(e, "fetching user by ID")
        }
    }

    override fun update(item: UserEntity) {
        try {
            val filter = Filters.eq("_id", item.id)
            val options = ReplaceOptions().upsert(false)
            val result = usersCollection.replaceOne(filter, item, options)

            if (result.matchedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("User not found with id: ${item.id}")
            }
        } catch (e: PlanMateException) {
            throw e
        } catch (e: Exception) {
            handleMongoException(e, "updating user")
        }
    }

    override fun delete(id: UUID) {
        try {
            val result = usersCollection.deleteOne(Filters.eq("_id", id))

            if (result.deletedCount == 0L) {
                throw PlanMateException.ItemNotFoundException("User not found with id: $id")
            }
        } catch (e: PlanMateException) {
            throw e
        } catch (e: Exception) {
            handleMongoException(e, "deleting user")
        }
    }
}