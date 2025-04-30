package data.csvfile

import org.example.data.DataProvider
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.utils.PlanMatException
import java.io.File
import java.io.IOException
import java.util.*


class UserCsvImpl(
    fileName: String
) : DataProvider<UserEntity> {
    private val file: File = File(fileName)

    override fun add(item: UserEntity) {
        try {
            val users = loadFromCsv().toMutableList()
            users.add(item)
            saveToCsv(users)
        } catch (e: Exception) {
            throw PlanMatException.FileWriteException("Error adding user: ${e.message}")
        }
    }

    override fun get(): List<UserEntity> = loadFromCsv()

    override fun getById(id: UUID): UserEntity? = loadFromCsv().find { it.id == id }

    override fun update(item: UserEntity) {
        val users = loadFromCsv().toMutableList()
        val index = users.indexOfFirst { it.id == item.id }

        if (index == -1) {
            throw PlanMatException.ItemNotFoundException("User with ID ${item.id} not found.")
        }

        users[index] = item
        try {
            saveToCsv(users)
        } catch (e: Exception) {
            throw PlanMatException.FileWriteException("Error updating user: ${e.message}")
        }
    }


    override fun delete(id: UUID) {
        val users = loadFromCsv().toMutableList()
        val userToDelete = users.find { it.id == id }

        if (userToDelete == null) {
            throw PlanMatException.ItemNotFoundException("User with ID $id not found.")
        }

        users.remove(userToDelete)
        try {
            saveToCsv(users)
        } catch (e: Exception) {
            throw PlanMatException.FileWriteException("Error deleting user: ${e.message}")
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
            throw PlanMatException.FileWriteException("Error creating file '${file.name}': ${e.message}")
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
            throw PlanMatException.FileWriteException("Error writing to file '${file.name}': ${e.message}")
        }
    }

    private fun fromCSVLine(line: String): UserEntity {
        try {
            val parts = line.split(",")
            return UserEntity(
                id = UUID.fromString(parts[0]),
                userName = parts[1],
                password = parts[2],
                type = UserType.valueOf(parts[3])
            )
        } catch (e: Exception) {
            throw PlanMatException.InvalidFormatException("Malformed CSV line: $line. ${e.message}")
        }
    }

    private fun toCSVLine(user: UserEntity): String {
        return "${user.id},${user.userName},${user.password},${user.type}"
    }
}