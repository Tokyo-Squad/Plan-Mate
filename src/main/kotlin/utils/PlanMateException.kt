package org.example.utils

open class PlanMateException(message: String) : Exception(message) {

    class FileWriteException(message: String = "Error writing to file.") : PlanMateException(message)

    class ItemNotFoundException(message: String = "Item not found.") : PlanMateException(message)

    class UnknownException(message: String = "An unknown error occurred.") : PlanMateException(message)

    class InvalidFormatException(message: String = "Invalid data format.") : PlanMateException(message)

    class ValidationException(message: String = "Validation failed.") : PlanMateException(message)

    class UserActionNotAllowedException(
        message: String = "This user is not allowed to perform this action."
    ) : PlanMateException(message)

    class InvalidStateIdException(message: String = "Invalid state id, no audit logs found.") : PlanMateException(message)
}