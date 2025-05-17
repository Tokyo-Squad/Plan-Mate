package org.example.data.local.csvfile

import kotlinx.datetime.LocalDateTime
import org.example.data.LocalDataSource
import org.example.data.util.exception.FileException
import logic.model.Task
import java.io.File
import java.io.IOException
import java.util.UUID

class TaskCsvImpl(
    fileName: String
) : LocalDataSource<Task> {

    private val file: File = File(fileName)

    override suspend fun add(item: Task) {
        try {
            val items = loadFromCsv().toMutableList()
            items.add(item)
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error adding task: ${e.message}")
        }
    }

    override suspend fun get(): List<Task> = loadFromCsv()

    override suspend fun getById(id: UUID): Task? = loadFromCsv().find { it.id == id }

    override suspend fun update(item: Task) {
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

    private fun loadFromCsv(): List<Task> {
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

    private fun readAndParseFile(): List<Task> {
        return file.readLines()
            .filter { it.isNotBlank() }
            .map { fromCSVLine(it) }
    }

    private fun saveToCsv(data: List<Task>) {
        try {
            val content = data.joinToString("\n") { toCSVLine(it) }
            file.writeText(content)
        } catch (e: IOException) {
            throw FileException.FileWriteException("Error writing to file '${file.name}': ${e.message}")
        }
    }

    private fun fromCSVLine(line: String): Task {
        try {
            val parts = line.split(",")
            return Task(
                id = UUID.fromString(parts[0]),
                title = parts[1],
                description = parts[2],
                workflowStateId = UUID.fromString(parts[3]),
                projectId = UUID.fromString(parts[4]),
                createdByUserId = UUID.fromString(parts[5]),
                createdAt = LocalDateTime.parse(parts[6])
            )
        } catch (e: Exception) {
            throw FileException.FileInvalidFormatException("Malformed CSV line: $line. ${e.message}")
        }
    }

    private fun toCSVLine(entity: Task): String {
        return "${entity.id},${entity.title},${entity.description},${entity.workflowStateId},${entity.projectId},${entity.createdByUserId},${entity.createdAt}"
    }
}