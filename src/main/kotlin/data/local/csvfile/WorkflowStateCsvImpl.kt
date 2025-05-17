package org.example.data.local.csvfile

import org.example.data.LocalDataSource
import org.example.data.util.exception.FileException
import domain.model.WorkflowState
import java.io.File
import java.io.IOException
import java.util.UUID

class WorkflowStateCsvImpl(
    fileName: String
) : LocalDataSource<WorkflowState> {

    private val file: File = File(fileName)

    override suspend fun add(item: WorkflowState) {
        try {
            val items = loadFromCsv().toMutableList()
            items.add(item)
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error adding state: ${e.message}")
        }
    }

    override suspend fun get(): List<WorkflowState> = loadFromCsv()

    override suspend fun getById(id: UUID): WorkflowState? = loadFromCsv().find { it.id == id }

    override suspend fun update(item: WorkflowState) {
        val items = loadFromCsv().toMutableList()
        val index = items.indexOfFirst { it.id == item.id }

        if (index == -1) {
            throw FileException.FileItemNotFoundException("State with ID ${item.id} not found.")
        }

        items[index] = item
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error updating state: ${e.message}")
        }
    }

    override suspend fun delete(id: UUID) {
        val items = loadFromCsv().toMutableList()
        val stateToDelete = items.find { it.id == id }

        if (stateToDelete == null) {
            throw FileException.FileItemNotFoundException("State with ID $id not found.")
        }

        items.remove(stateToDelete)
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error deleting state: ${e.message}")
        }
    }

    private fun loadFromCsv(): List<WorkflowState> {
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

    private fun readAndParseFile(): List<WorkflowState> {
        return file.readLines()
            .filter { it.isNotBlank() }
            .map { fromCSVLine(it) }
    }

    private fun saveToCsv(data: List<WorkflowState>) {
        try {
            val content = data.joinToString("\n") { toCSVLine(it) }
            file.writeText(content)
        } catch (e: IOException) {
            throw FileException.FileWriteException("Error writing to file '${file.name}': ${e.message}")
        }
    }

    private fun fromCSVLine(line: String): WorkflowState {
        try {
            val parts = line.split(",")
            return WorkflowState(
                id = UUID.fromString(parts[0]),
                name = parts[1],
                projectId = UUID.fromString(parts[2])
            )
        } catch (e: Exception) {
            throw FileException.FileInvalidFormatException("Malformed CSV line: $line. ${e.message}")
        }
    }

    private fun toCSVLine(entity: WorkflowState): String {
        return "${entity.id},${entity.name},${entity.projectId}"
    }
}