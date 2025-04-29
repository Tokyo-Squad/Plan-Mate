package data.csvfile

import org.example.data.DataProvider
import org.example.entity.StateEntity
import org.example.utils.PlanMatException
import java.io.File
import java.io.IOException
import java.util.*

class StateCsvImpl(
    fileName: String
) : DataProvider<StateEntity> {

    private val file: File = File(fileName)

    override fun add(item: StateEntity) {
        try {
            val items = loadFromCsv().toMutableList()
            items.add(item)
            saveToCsv(items)
        } catch (e: Exception) {
            throw PlanMatException.FileWriteException("Error adding state: ${e.message}")
        }
    }

    override fun get(): List<StateEntity> = loadFromCsv()

    override fun getById(id: UUID): StateEntity? = loadFromCsv().find { it.id == id }

    override fun update(item: StateEntity) {
        val items = loadFromCsv().toMutableList()
        val index = items.indexOfFirst { it.id == item.id }

        if (index == -1) {
            throw PlanMatException.ItemNotFoundException("State with ID ${item.id} not found.")
        }

        items[index] = item
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw PlanMatException.FileWriteException("Error updating state: ${e.message}")
        }
    }

    override fun delete(id: UUID) {
        val items = loadFromCsv().toMutableList()
        val stateToDelete = items.find { it.id == id }

        if (stateToDelete == null) {
            throw PlanMatException.ItemNotFoundException("State with ID $id not found.")
        }

        items.remove(stateToDelete)
        try {
            saveToCsv(items)
        } catch (e: Exception) {
            throw PlanMatException.FileWriteException("Error deleting state: ${e.message}")
        }
    }

    private fun loadFromCsv(): List<StateEntity> {
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                throw PlanMatException.FileWriteException("Error creating file '${file.name}': ${e.message}")
            }
            return emptyList()
        }

        return try {
            file.readLines()
                .filter { it.isNotBlank() }
                .map { fromCSVLine(it) }
        } catch (e: IOException) {
            throw PlanMatException.FileReadException("Error reading file '${file.name}': ${e.message}")
        }
    }

    private fun saveToCsv(data: List<StateEntity>) {
        try {
            val content = data.joinToString("\n") { toCSVLine(it) }
            file.writeText(content)
        } catch (e: IOException) {
            throw PlanMatException.FileWriteException("Error writing to file '${file.name}': ${e.message}")
        }
    }

    private fun fromCSVLine(line: String): StateEntity {
        val parts = line.split(",")
        return StateEntity(
            id = UUID.fromString(parts[0]),
            name = parts[1],
            projectId = UUID.fromString(parts[2])
        )
    }

    private fun toCSVLine(entity: StateEntity): String {
        return "${entity.id},${entity.name},${entity.projectId}"
    }
}