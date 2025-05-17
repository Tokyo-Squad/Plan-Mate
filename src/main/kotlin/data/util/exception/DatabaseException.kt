package org.example.data.util.exception

open class DatabaseException(message: String) : Exception(message) {

    class DatabaseOperationException(
        message: String = "Database operation failed."
    ) : DatabaseException(message)

    class DatabaseTimeoutException(
        message: String = "Database operation timed out."
    ) : DatabaseException(message)

    class DuplicateKeyException(
        message: String = "Duplicate key violation in database."
    ) : DatabaseException(message)

    class DatabaseAuthenticationException(
        message: String = "Database authentication failed."
    ) : DatabaseException(message)

    class DatabaseItemNotFoundException(message: String = "Item not found.") : DatabaseException(message)

    class UnknownException(
        message: String = "An unknown error occurred."
    ) : DatabaseException(message)

}