package org.example.data.local.csvfile

import kotlinx.datetime.LocalDateTime
import org.example.data.LocalDataSource
import org.example.data.util.exception.FileException
import org.example.entity.ProjectEntity
import java.io.File
import java.io.IOException
import java.util.UUID

class ProjectCsvImpl(
    fileName: String
) : LocalDataSource<ProjectEntity> {

    private val file: File = File(fileName)

    override suspend fun add(item: ProjectEntity) {
        try {
            val items = loadFromCsv().toMutableList()
            items.add(item)
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error adding project: ${e.message}")
        }
    }

    override suspend fun get(): List<ProjectEntity> = loadFromCsv()

    override suspend fun getById(id: UUID): ProjectEntity? = loadFromCsv().find { it.id == id }

    override suspend fun update(item: ProjectEntity) {
        val items = loadFromCsv().toMutableList()
        val index = items.indexOfFirst { it.id == item.id }

        if (index == -1) {
            throw FileException.FileItemNotFoundException("Project with ID ${item.id} not found.")
        }

        items[index] = item
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error updating project: ${e.message}")
        }
    }

    override suspend fun delete(id: UUID) {
        val items = loadFromCsv().toMutableList()
        val projectToDelete = items.find { it.id == id }

        if (projectToDelete == null) {
            throw FileException.FileItemNotFoundException("Project with ID $id not found.")
        }

        items.remove(projectToDelete)
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error deleting project: ${e.message}")
        }
    }

    private fun loadFromCsv(): List<ProjectEntity> {
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

    private fun readAndParseFile(): List<ProjectEntity> {
        return file.readLines()
            .filter { it.isNotBlank() }
            .map { fromCSVLine(it) }
    }


    private fun saveToCsv(data: List<ProjectEntity>) {
        try {
            val content = data.joinToString("\n") { toCSVLine(it) }
            file.writeText(content)
        } catch (e: IOException) {
            throw FileException.FileWriteException("Error writing to file '${file.name}': ${e.message}")
        }
    }

    private fun fromCSVLine(line: String): ProjectEntity {
        try {
            val parts = line.split(",")
            return ProjectEntity(
                id = UUID.fromString(parts[0]),
                name = parts[1],
                createdByAdminId = UUID.fromString(parts[2]),
                createdAt = LocalDateTime.parse(parts[3])
            )
        } catch (e: Exception) {
            throw FileException.FileInvalidFormatException("Malformed CSV line: $line. ${e.message}")
        }
    }

    private fun toCSVLine(entity: ProjectEntity): String {
        return "${entity.id},${entity.name},${entity.createdByAdminId},${entity.createdAt}"
    }
}