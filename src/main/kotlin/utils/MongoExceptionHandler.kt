package org.example.utils

import com.mongodb.MongoException
import com.mongodb.MongoSecurityException
import com.mongodb.MongoTimeoutException
import com.mongodb.MongoWriteException

object MongoExceptionHandler {
    /**
     * Handles MongoDB exceptions and converts them to appropriate PlanMateExceptions
     *
     * @param e The caught exception
     * @param operation Description of the operation being performed
     * @throws PlanMateException with appropriate subtype based on the error
     */
    private fun handle(e: Exception, operation: String): Nothing {
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

    /**
     * Wraps a MongoDB operation in a try-catch block with standardized error handling
     *
     * @param operation Description of the operation being performed
     * @param block The operation to execute
     * @return Result of the operation
     */
    suspend fun <T> handleOperation(operation: String, block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            handle(e, operation)
        }
    }
}