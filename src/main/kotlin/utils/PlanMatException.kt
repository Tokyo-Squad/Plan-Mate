package org.example.utils

open class PlanMatException(message: String) : Exception(message) {

    class FileWriteException(message: String = "Error writing to file.") : PlanMatException(message)

    class ItemNotFoundException(message: String = "Item not found.") : PlanMatException(message)

    class UnknownException(message: String = "An unknown error occurred.") : PlanMatException(message)

    class InvalidFormatException(message: String = "Invalid data format.") : PlanMatException(message)

    class ValidationException(message: String = "Validation failed.") : PlanMatException(message)

    class UserActionNotAllowedException(
        message: String = "This user is not allowed to perform this action."
    ) : PlanMatException(message)

    class InvalidStateIdException(message: String = "Invalid state id, no audit logs found.") : PlanMatException(message)
}