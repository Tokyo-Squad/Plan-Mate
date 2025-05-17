package org.example.data.util.exception

import com.mongodb.MongoException
import com.mongodb.MongoSecurityException
import com.mongodb.MongoTimeoutException
import com.mongodb.MongoWriteException

object MongoExceptionHandler {

    private fun handle(e: Exception, operation: String): Nothing {
        throw when (e) {
            is MongoWriteException -> {
                when (e.error.code) {
                    11000 -> DatabaseException.DuplicateKeyException(
                        "Duplicate key error during $operation: ${e.message}"
                    )

                    else -> DatabaseException.DatabaseOperationException(
                        "Write error during $operation: ${e.message}"
                    )
                }
            }

            is MongoTimeoutException -> DatabaseException.DatabaseTimeoutException(
                "Operation timeout during $operation: ${e.message}"
            )

            is MongoSecurityException -> DatabaseException.DatabaseAuthenticationException(
                "Authentication failed during $operation: ${e.message}"
            )

            is MongoException -> DatabaseException.DatabaseOperationException(
                "Database error during $operation: ${e.message}"
            )

            else -> DatabaseException.UnknownException(
                "Unexpected error during $operation: ${e.message}"
            )
        }
    }

    suspend fun <T> handleOperation(operation: String, block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            handle(e, operation)
        }
    }
}