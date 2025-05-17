package org.example.data.local.csvfile

import org.example.data.LocalDataSource
import org.example.data.util.exception.FileException
import org.example.entity.UserEntity
import org.example.entity.UserType
import java.io.File
import java.io.IOException
import java.util.UUID


class UserCsvImpl(
    fileName: String
) : LocalDataSource<UserEntity> {
    private val file: File = File(fileName)

    override suspend fun add(item: UserEntity) {
        try {
            val users = loadFromCsv().toMutableList()
            users.add(item)
            saveToCsv(users)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error adding user: ${e.message}")
        }
    }

    override suspend fun get(): List<UserEntity> = loadFromCsv()

    override suspend fun getById(id: UUID): UserEntity? = loadFromCsv().find { it.id == id }

    override suspend fun update(item: UserEntity) {
        val users = loadFromCsv().toMutableList()
        val index = users.indexOfFirst { it.id == item.id }

        if (index == -1) {
            throw FileException.FileItemNotFoundException("User with ID ${item.id} not found.")
        }

        users[index] = item
        try {
            saveToCsv(users)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error updating user: ${e.message}")
        }
    }


    override suspend fun delete(id: UUID) {
        val users = loadFromCsv().toMutableList()
        val userToDelete = users.find { it.id == id }

        if (userToDelete == null) {
            throw FileException.FileItemNotFoundException("User with ID $id not found.")
        }

        users.remove(userToDelete)
        try {
            saveToCsv(users)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error deleting user: ${e.message}")
        }
    }

    private fun loadFromCsv(): List<UserEntity> {
        ensureFileExists()
        return readAndParseFile()
    }

    private fun ensureFileExists() {
        if (file.exists()) return
        try {
            file.createNewFile()
        } catch (e: IOException) {
            throw FileException.FileWriteException("Error creating file '${file.name}': ${e.message}")
        }
    }

    private fun readAndParseFile(): List<UserEntity> {
        return file.readLines()
            .filter { it.isNotBlank() }
            .map { fromCSVLine(it) }
    }

    private fun saveToCsv(data: List<UserEntity>) {
        try {
            val content = data.joinToString("\n") { toCSVLine(it) }
            file.writeText(content)
        } catch (e: IOException) {
            throw FileException.FileWriteException("Error writing to file '${file.name}': ${e.message}")
        }
    }

    private fun fromCSVLine(line: String): UserEntity {
        try {
            val parts = line.split(",")
            return UserEntity(
                id = UUID.fromString(parts[0]),
                username = parts[1],
                password = parts[2],
                type = UserType.valueOf(parts[3])
            )
        } catch (e: Exception) {
            throw FileException.FileInvalidFormatException("Malformed CSV line: $line. ${e.message}")
        }
    }

    private fun toCSVLine(user: UserEntity): String {
        return "${user.id},${user.username},${user.password},${user.type}"
    }
}