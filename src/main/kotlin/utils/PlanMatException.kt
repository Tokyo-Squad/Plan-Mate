package org.example.utils

open class PlanMatException(message: String) : Exception(message) {

    class FileWriteException(message: String = "Error writing to file.") : PlanMatException(message)

    class ItemNotFoundException(message: String = "Item not found.") : PlanMatException(message)

    class UnknownException(message: String = "An unknown error occurred.") : PlanMatException(message)

    class InvalidFormatException(message: String = "Invalid data format.") : PlanMatException(message)

}