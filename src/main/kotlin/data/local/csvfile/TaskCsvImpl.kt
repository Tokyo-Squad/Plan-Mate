package org.example.data.local.csvfile

import kotlinx.datetime.LocalDateTime
import org.example.data.LocalDataSource
import org.example.data.util.exception.FileException
import org.example.entity.TaskEntity
import java.io.File
import java.io.IOException
import java.util.UUID

class TaskCsvImpl(
    fileName: String
) : LocalDataSource<TaskEntity> {

    private val file: File = File(fileName)

    override suspend fun add(item: TaskEntity) {
        try {
            val items = loadFromCsv().toMutableList()
            items.add(item)
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error adding task: ${e.message}")
        }
    }

    override suspend fun get(): List<TaskEntity> = loadFromCsv()

    override suspend fun getById(id: UUID): TaskEntity? = loadFromCsv().find { it.id == id }

    override suspend fun update(item: TaskEntity) {
        val items = loadFromCsv().toMutableList()
        val index = items.indexOfFirst { it.id == item.id }

        if (index == -1) {
            throw FileException.FileItemNotFoundException("Task with ID ${item.id} not found.")
        }

        items[index] = item
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error updating task: ${e.message}")
        }
    }

    override suspend fun delete(id: UUID) {
        val items = loadFromCsv().toMutableList()
        val taskToDelete = items.find { it.id == id }

        if (taskToDelete == null) {
            throw FileException.FileItemNotFoundException("Task with ID $id not found.")
        }

        items.remove(taskToDelete)
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error deleting task: ${e.message}")
        }
    }

    private fun loadFromCsv(): List<TaskEntity> {
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

    private fun readAndParseFile(): List<TaskEntity> {
        return file.readLines()
            .filter { it.isNotBlank() }
            .map { fromCSVLine(it) }
    }

    private fun saveToCsv(data: List<TaskEntity>) {
        try {
            val content = data.joinToString("\n") { toCSVLine(it) }
            file.writeText(content)
        } catch (e: IOException) {
            throw FileException.FileWriteException("Error writing to file '${file.name}': ${e.message}")
        }
    }

    private fun fromCSVLine(line: String): TaskEntity {
        try {
            val parts = line.split(",")
            return TaskEntity(
                id = UUID.fromString(parts[0]),
                title = parts[1],
                description = parts[2],
                stateId = UUID.fromString(parts[3]),
                projectId = UUID.fromString(parts[4]),
                createdByUserId = UUID.fromString(parts[5]),
                createdAt = LocalDateTime.parse(parts[6])
            )
        } catch (e: Exception) {
            throw FileException.FileInvalidFormatException("Malformed CSV line: $line. ${e.message}")
        }
    }

    private fun toCSVLine(entity: TaskEntity): String {
        return "${entity.id},${entity.title},${entity.description},${entity.stateId},${entity.projectId},${entity.createdByUserId},${entity.createdAt}"
    }
}