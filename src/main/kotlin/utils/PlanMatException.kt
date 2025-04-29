package org.example.utils

sealed class PlanMatException(message: String) : Exception(message) {

    class FileReadException(message: String = "Error reading file.") : PlanMatException(message)

    class FileWriteException(message: String = "Error writing to file.") : PlanMatException(message)

    class ItemNotFoundException(message: String = "Item not found.") : PlanMatException(message)

    class UnknownException(message: String = "An unknown error occurred.") : PlanMatException(message)
}
