package data.csvfile

import org.example.data.DataProvider
import org.example.entity.StateEntity
import org.example.utils.PlanMateException
import java.io.File
import java.io.IOException
import java.util.*

class StateCsvImpl(
    fileName: String
) : DataProvider<StateEntity> {

    private val file: File = File(fileName)

    override suspend fun add(item: StateEntity) {
        try {
            val items = loadFromCsv().toMutableList()
            items.add(item)
            saveToCsv(items)
        } catch (e: Exception) {
            throw PlanMateException.FileWriteException("Error adding state: ${e.message}")
        }
    }

    override suspend fun get(): List<StateEntity> = loadFromCsv()

    override suspend fun getById(id: UUID): StateEntity? = loadFromCsv().find { it.id == id }

    override suspend fun update(item: StateEntity) {
        val items = loadFromCsv().toMutableList()
        val index = items.indexOfFirst { it.id == item.id }

        if (index == -1) {
            throw PlanMateException.ItemNotFoundException("State with ID ${item.id} not found.")
        }

        items[index] = item
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw PlanMateException.FileWriteException("Error updating state: ${e.message}")
        }
    }

    override suspend fun delete(id: UUID) {
        val items = loadFromCsv().toMutableList()
        val stateToDelete = items.find { it.id == id }

        if (stateToDelete == null) {
            throw PlanMateException.ItemNotFoundException("State with ID $id not found.")
        }

        items.remove(stateToDelete)
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw PlanMateException.FileWriteException("Error deleting state: ${e.message}")
        }
    }

    private fun loadFromCsv(): List<StateEntity> {
        ensureFileExists()
        return readAndParseFile()
    }

    private fun ensureFileExists() {
        if (file.exists()) return
        try {
            file.createNewFile()
        } catch (e: IOException) {
            throw PlanMateException.FileWriteException("Error creating file '${file.name}': ${e.message}")
        }
    }

    private fun readAndParseFile(): List<StateEntity> {
        return file.readLines()
            .filter { it.isNotBlank() }
            .map { fromCSVLine(it) }
    }

    private fun saveToCsv(data: List<StateEntity>) {
        try {
            val content = data.joinToString("\n") { toCSVLine(it) }
            file.writeText(content)
        } catch (e: IOException) {
            throw PlanMateException.FileWriteException("Error writing to file '${file.name}': ${e.message}")
        }
    }
    private fun fromCSVLine(line: String): StateEntity {
        try {
            val parts = line.split(",")
            return StateEntity(
                id = UUID.fromString(parts[0]),
                name = parts[1],
                projectId = UUID.fromString(parts[2])
            )
        } catch (e: Exception) {
            throw PlanMateException.InvalidFormatException("Malformed CSV line: $line. ${e.message}")
        }
    }

    private fun toCSVLine(entity: StateEntity): String {
        return "${entity.id},${entity.name},${entity.projectId}"
    }
}