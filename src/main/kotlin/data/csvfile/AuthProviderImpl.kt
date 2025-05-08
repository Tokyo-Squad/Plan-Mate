package org.example.data.csvfile

import org.example.data.AuthProvider
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.utils.PlanMateException
import java.io.File
import java.io.IOException
import java.util.*

class AuthProviderImpl(
    fileName: String
) : AuthProvider {

    private val file = File(fileName)

    override suspend fun addCurrentUser(user: UserEntity) {
        ensureFileExists()
        try {
            file.writeText(toCSVLine(user))
        } catch (e: IOException) {
            throw PlanMateException.FileWriteException("Error writing current user to file: ${e.message}")
        }
    }

    override suspend fun deleteCurrentUser() {
        ensureFileExists()
        try {
            file.writeText("")
            file.delete()
        } catch (e: IOException) {
            throw PlanMateException.FileWriteException("Error deleting current user: ${e.message}")
        }
    }

    override suspend fun getCurrentUser(): UserEntity {
        ensureFileExists()
        val content = file.readText().trim()
        if (content.isBlank()) {
            throw PlanMateException.ItemNotFoundException("No current user found.")
        }
        return try {
            fromCSVLine(content)
        } catch (e: Exception) {
            throw PlanMateException.InvalidFormatException("Malformed current user data: ${e.message}")
        }
    }

    private fun toCSVLine(user: UserEntity): String {
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
            throw PlanMateException.InvalidFormatException("Malformed CSV line: $line. ${e.message}")
        }
    }

    private fun ensureFileExists() {
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                throw PlanMateException.FileWriteException("Could not create file: ${e.message}")
            }
        }
    }
}