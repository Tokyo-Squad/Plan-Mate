package org.example.data.local.csvfile

import kotlinx.datetime.LocalDateTime
import org.example.data.LocalDataSource
import org.example.data.util.exception.FileException
import domain.model.AuditAction
import domain.model.AuditLog
import domain.model.AuditedType
import java.io.File
import java.io.IOException
import java.util.UUID

class AuditLogCsvImpl(
    fileName: String
) : LocalDataSource<AuditLog> {

    private val file: File = File(fileName)

    override suspend fun add(item: AuditLog) {
        try {
            val items = loadFromCsv().toMutableList()
            items.add(item)
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error adding audit log: ${e.message}")
        }
    }

    override suspend fun get(): List<AuditLog> = loadFromCsv()

    override suspend fun getById(id: UUID): AuditLog? = loadFromCsv().find { it.id == id }

    override suspend fun update(item: AuditLog) {
        val items = loadFromCsv().toMutableList()
        val index = items.indexOfFirst { it.id == item.id }

        if (index == -1) {
            throw FileException.FileItemNotFoundException("Audit log with ID ${item.id} not found.")
        }

        items[index] = item
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error updating audit log: ${e.message}")
        }
    }

    override suspend fun delete(id: UUID) {
        val items = loadFromCsv().toMutableList()
        val logToDelete = items.find { it.id == id }

        if (logToDelete == null) {
            throw FileException.FileItemNotFoundException("Audit log with ID $id not found.")
        }

        items.remove(logToDelete)
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw FileException.FileWriteException("Error deleting audit log: ${e.message}")
        }
    }

    private fun loadFromCsv(): List<AuditLog> {
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

    private fun readAndParseFile(): List<AuditLog> {
        return file.readLines()
            .filter { it.isNotBlank() }
            .map { fromCSVLine(it) }
    }

    private fun saveToCsv(data: List<AuditLog>) {
        try {
            val content = data.joinToString("\n") { toCSVLine(it) }
            file.writeText(content)
        } catch (e: IOException) {
            throw FileException.FileWriteException("Error writing to file '${file.name}': ${e.message}")
        }
    }

    private fun fromCSVLine(line: String): AuditLog {
        try {
            val parts = line.split(",")
            return AuditLog(
                id = UUID.fromString(parts[0]),
                userId = UUID.fromString(parts[1]),
                entityType = AuditedType.valueOf(parts[2]),
                entityId = UUID.fromString(parts[3]),
                action = AuditAction.valueOf(parts[4]),
                changeDetails = parts[5],
                timestamp = LocalDateTime.parse(parts[6])
            )
        } catch (e: Exception) {
            throw FileException.FileInvalidFormatException("Malformed CSV line: $line. ${e.message}")
        }
    }

    private fun toCSVLine(entity: AuditLog): String {
        return "${entity.id},${entity.userId},${entity.entityType},${entity.entityId},${entity.action},${entity.changeDetails},${entity.timestamp}"
    }
}