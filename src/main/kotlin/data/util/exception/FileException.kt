package org.example.data.util.exception

open class FileException(message: String) : Exception(message) {
    class FileWriteException(message: String = "Error writing to file.") : FileException(message)
    class FileItemNotFoundException(message: String = "Item not found.") : FileException(message)
    class FileInvalidFormatException(message: String = "Invalid data format.") : FileException(message)
}