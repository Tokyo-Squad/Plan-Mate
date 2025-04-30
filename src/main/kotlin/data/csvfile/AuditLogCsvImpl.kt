package data.csvfile

import kotlinx.datetime.LocalDateTime
import org.example.data.DataProvider
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.utils.PlanMatException
import java.io.File
import java.io.IOException
import java.util.*

class AuditLogCsvImpl(
    fileName: String
) : DataProvider<AuditLogEntity> {

    private val file: File = File(fileName)

    override fun add(item: AuditLogEntity) {
        try {
            val items = loadFromCsv().toMutableList()
            items.add(item)
            saveToCsv(items)
        } catch (e: Exception) {
            throw PlanMatException.FileWriteException("Error adding audit log: ${e.message}")
        }
    }

    override fun get(): List<AuditLogEntity> = loadFromCsv()

    override fun getById(id: UUID): AuditLogEntity? = loadFromCsv().find { it.id == id }

    override fun update(item: AuditLogEntity) {
        val items = loadFromCsv().toMutableList()
        val index = items.indexOfFirst { it.id == item.id }

        if (index == -1) {
            throw PlanMatException.ItemNotFoundException("Audit log with ID ${item.id} not found.")
        }

        items[index] = item
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw PlanMatException.FileWriteException("Error updating audit log: ${e.message}")
        }
    }

    override fun delete(id: UUID) {
        val items = loadFromCsv().toMutableList()
        val logToDelete = items.find { it.id == id }

        if (logToDelete == null) {
            throw PlanMatException.ItemNotFoundException("Audit log with ID $id not found.")
        }

        items.remove(logToDelete)
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw PlanMatException.FileWriteException("Error deleting audit log: ${e.message}")
        }
    }

    private fun loadFromCsv(): List<AuditLogEntity> {
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

    private fun readAndParseFile(): List<AuditLogEntity> {
           return file.readLines()
                .filter { it.isNotBlank() }
                .map { fromCSVLine(it) }
    }

    private fun saveToCsv(data: List<AuditLogEntity>) {
        try {
            val content = data.joinToString("\n") { toCSVLine(it) }
            file.writeText(content)
        } catch (e: IOException) {
            throw PlanMatException.FileWriteException("Error writing to file '${file.name}': ${e.message}")
        }
    }

    private fun fromCSVLine(line: String): AuditLogEntity {
        try {
            val parts = line.split(",")
            return AuditLogEntity(
                id = UUID.fromString(parts[0]),
                userId = UUID.fromString(parts[1]),
                entityType = AuditedEntityType.valueOf(parts[2]),
                entityId = UUID.fromString(parts[3]),
                action = AuditAction.valueOf(parts[4]),
                changeDetails = parts[5],
                timestamp = LocalDateTime.parse(parts[6])
            )
        } catch (e: Exception) {
            throw PlanMatException.InvalidFormatException("Malformed CSV line: $line. ${e.message}")
        }
    }

    private fun toCSVLine(entity: AuditLogEntity): String {
        return "${entity.id},${entity.userId},${entity.entityType},${entity.entityId},${entity.action},${entity.changeDetails},${entity.timestamp}"
    }
}