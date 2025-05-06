package org.example.utils

open class PlanMateException(message: String) : Exception(message) {

    class FileWriteException(message: String = "Error writing to file.") : PlanMateException(message)

    class ItemNotFoundException(message: String = "Item not found.") : PlanMateException(message)

    class UnknownException(message: String = "An unknown error occurred.") : PlanMateException(message)

    class InvalidFormatException(message: String = "Invalid data format.") : PlanMateException(message)

    class ValidationException(message: String = "Validation failed.") : PlanMateException(message)

    class DatabaseException(message: String = "Error adding project") : PlanMateException(message)

    class AuthenticationException(message: String = "Problem in User/Password input") : PlanMateException(message)

    class HashingException(message: String = "Failed to hash") : PlanMateException(message)

    class UserActionNotAllowedException(
        message: String = "This user is not allowed to perform this action."
    ) : PlanMateException(message)

    class InvalidStateIdException(message: String = "Invalid state id, no audit logs found.") :
        PlanMateException(message)

    // MongoDB Exceptions
    class DatabaseConnectionException(
        message: String = "Failed to connect to database."
    ) : PlanMateException(message)

    class DatabaseOperationException(
        message: String = "Database operation failed."
    ) : PlanMateException(message)

    class DatabaseTimeoutException(
        message: String = "Database operation timed out."
    ) : PlanMateException(message)

    class DuplicateKeyException(
        message: String = "Duplicate key violation in database."
    ) : PlanMateException(message)

    class DatabaseAuthenticationException(
        message: String = "Database authentication failed."
    ) : PlanMateException(message)

    class DatabaseTransactionException(
        message: String = "Database transaction failed."
    ) : PlanMateException(message)
}