package org.example.data.local.csvfile

import org.example.data.Authentication
import org.example.data.remote.dto.UserDto
import org.example.data.util.exception.FileException
import org.example.data.util.mapper.toUserDto
import org.example.entity.UserEntity
import org.example.entity.UserType
import java.io.File
import java.io.IOException
import java.util.UUID

class AuthCsvImpl(
    fileName: String
) : Authentication {

    private val file = File(fileName)

    override suspend fun addCurrentUser(user: UserDto) {
        ensureFileExists()
        try {
            file.writeText(toCSVLine(user))
        } catch (e: IOException) {
            throw FileException.FileWriteException("Error writing current user to file: ${e.message}")
        }
    }

    override suspend fun deleteCurrentUser() {
        ensureFileExists()
        try {
            file.writeText("")
            file.delete()
        } catch (e: IOException) {
            throw FileException.FileWriteException("Error deleting current user: ${e.message}")
        }
    }

    override suspend fun getCurrentUser(): UserDto {
        ensureFileExists()
        val content = file.readText().trim()
        if (content.isBlank()) {
            throw FileException.FileItemNotFoundException("No current user found.")
        }
        return try {
            fromCSVLine(content).toUserDto()
        } catch (e: Exception) {
            throw FileException.FileInvalidFormatException("Malformed current user data: ${e.message}")
        }
    }

    private fun toCSVLine(user: UserDto): String {
        return "${user.id},${user.username},${user.password},${user.type}"
    }

    private fun fromCSVLine(line: String): UserEntity {
        val parts = line.split(",")
        return try {
            UserEntity(
                id = UUID.fromString(parts[0]),
                username = parts[1],
                password = parts[2],
                type = UserType.valueOf(parts[3])
            )
        } catch (e: Exception) {
            throw FileException.FileInvalidFormatException("Malformed CSV line: $line. ${e.message}")
        }
    }

    private fun ensureFileExists() {
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                throw FileException.FileWriteException("Could not create file: ${e.message}")
            }
        }
    }
}