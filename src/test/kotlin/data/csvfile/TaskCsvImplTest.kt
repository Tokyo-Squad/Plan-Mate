package data.csvfile

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.example.entity.TaskEntity
import org.example.utils.PlanMateException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TaskCsvImplTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var file: File
    private lateinit var taskCsv: TaskCsvImpl
    private lateinit var task: TaskEntity

    @BeforeEach
    fun setup() {
        file = File(tempDir, "tasks.csv")
        taskCsv = TaskCsvImpl(file.absolutePath)
        task = TaskEntity(
            id = UUID.randomUUID(),
            title = "Initial Task",
            description = "Task description",
            stateId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            createdByUserId = UUID.randomUUID(),
            createdAt = LocalDateTime.parse("2025-04-29T15:00:00")
        )
    }

    @Test
    fun shouldReturnEntity_whenAddTask() {
        // When
        taskCsv.add(task)

        // Then
        val all = taskCsv.get()
        assertThat(all).hasSize(1)
    }

    @Test
    fun shouldReturnEntityById_whenExists() {
        // Given
        taskCsv.add(task)

        // When
        val result = taskCsv.getById(task.id)

        // Then
        assertThat(result).isEqualTo(task)
    }

    @Test
    fun shouldUpdateEntity_whenIdExists() {
        // Given
        taskCsv.add(task)
        val updated = task.copy(title = "Updated Task")

        // When
        taskCsv.update(updated)

        // Then
        val result = taskCsv.getById(updated.id)
        assertThat(result?.title).isEqualTo("Updated Task")
    }

    @Test
    fun shouldReturnNull_whenFileIsEmptyAndIdNotFound() {
        // Given
        file.writeText("")

        // When
        val result = taskCsv.getById(UUID.randomUUID())

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun shouldThrowItemNotFound_whenUpdatingNonExistentEntity() {
        // Given
        val nonExistent = task.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<PlanMateException.ItemNotFoundException> {
            taskCsv.update(nonExistent)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun ensureFileExists_shouldReturn_whenFileAlreadyExists() {
        // Given
        file.createNewFile()
        assertThat(file.exists()).isTrue()

        val taskCsv = TaskCsvImpl(file.absolutePath)

        // When
        taskCsv.add(task)

        // Then
        assertThat(file.exists()).isTrue()
    }

    @Test
    fun shouldDeleteEntity_whenIdExists() {
        // Given
        taskCsv.add(task)

        // When
        taskCsv.delete(task.id)

        // Then
        assertThat(taskCsv.get()).isEmpty()
    }

    @Test
    fun shouldReturnEmptyList_whenFileIsEmpty() {
        // Given
        file.writeText("")

        // When
        val result = taskCsv.get()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun shouldThrowFileWriteException_whenFileCreationFails() {
        // Given
        val nonExistentDir = File(tempDir, "non_existent_dir")
        val failingFile = File(nonExistentDir, "tasks.csv")

        // When
        val failingCsv = TaskCsvImpl(failingFile.absolutePath)

        // Then
        val exception = assertFailsWith<PlanMateException.FileWriteException> {
            failingCsv.add(task)
        }
        assertThat(exception).hasMessageThat().contains("Error creating file")
    }

    @Test
    fun shouldThrowException_whenCsvLineIsMalformed() {
        // Given
        file.writeText("invalid,line,also-invalid")

        // Then
        val exception = assertFailsWith<PlanMateException.InvalidFormatException> {
            taskCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun shouldThrowFileWriteException_whenWriteFails() {
        // Given
        val readOnlyFile = File(tempDir, "tasks.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = TaskCsvImpl(readOnlyFile.absolutePath)

        // When
        val exception = assertFailsWith<PlanMateException.FileWriteException> {
            failingCsv.add(task)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error writing to file")
    }

    @Test
    fun shouldThrowFileReadException_whenFileContentIsMalformed() {
        // Given
        file.writeText("invalid,line,malformed,content")

        // When / Then
        val exception = assertFailsWith<PlanMateException.InvalidFormatException> {
            taskCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun shouldSuccessfullyParseFile_whenValidContent() {
        // Given
        val validTask = TaskEntity(
            id = UUID.randomUUID(),
            title = "Valid Task",
            description = "Valid Task Description",
            stateId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            createdByUserId = UUID.randomUUID(),
            createdAt = LocalDateTime.parse("2025-04-29T15:00:00")
        )
        file.writeText("${validTask.id},${validTask.title},${validTask.description},${validTask.stateId},${validTask.projectId},${validTask.createdByUserId},${validTask.createdAt}")

        // When
        val result = taskCsv.get()

        // Then
        assertThat(result.first()).isEqualTo(validTask)
    }

    @Test
    fun shouldReturnNotEmpty_whenFileContainsEmptyLines() {
        // Given
        val validTask = TaskEntity(
            id = UUID.randomUUID(),
            title = "Valid Task",
            description = "Task description",
            stateId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            createdByUserId = UUID.randomUUID(),
            createdAt = LocalDateTime.parse("2025-04-29T15:00:00")
        )
        file.writeText("\n\n${validTask.id},${validTask.title},${validTask.description},${validTask.stateId},${validTask.projectId},${validTask.createdByUserId},${validTask.createdAt}\n\n") // Contains empty lines before and after the valid task data

        // When
        val result = taskCsv.get()

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result.first()).isEqualTo(validTask)
    }

    @Test
    fun shouldThrowItemNotFoundException_whenUpdatingNonExistentEntity() {
        // Given
        val nonExistentTask = task.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<PlanMateException.ItemNotFoundException> {
            taskCsv.update(nonExistentTask)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldThrowItemNotFound_whenUpdatingNonExistentTask() {
        // Given
        val nonExistentTask = task.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<PlanMateException.ItemNotFoundException> {
            taskCsv.update(nonExistentTask)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldThrowException_whenCreatedByAdminIdIsInvalidUUID() {
        // Given
        file.writeText("${UUID.randomUUID()},Test,invalid-uuid,2025-04-29T15:00:00")

        // When / Then
        val exception = assertFailsWith<PlanMateException.InvalidFormatException> {
            taskCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun shouldThrowFileWriteException_whenDeleteFails() {
        // Given
        taskCsv.add(task)
        val readOnlyFile = File(tempDir, "tasks.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = TaskCsvImpl(readOnlyFile.absolutePath)

        // When
        val exception = assertFailsWith<PlanMateException.FileWriteException> {
            failingCsv.delete(task.id)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error deleting task")
    }

    @Test
    fun shouldThrowFileWriteException_whenUpdateFails() {
        // Given
        taskCsv.add(task)
        val readOnlyFile = File(tempDir, "tasks.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = TaskCsvImpl(readOnlyFile.absolutePath)

        val updatedTask = task.copy(title = "Updated Task Title")

        // When
        val exception = assertFailsWith<PlanMateException.FileWriteException> {
            failingCsv.update(updatedTask)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error updating task")
    }

    @Test
    fun shouldThrowItemNotFound_whenDeletingNonExistentEntityInNonEmptyFile() {
        // Given
        taskCsv.add(task)

        // When
        val exception = assertFailsWith<PlanMateException.ItemNotFoundException> {
            taskCsv.delete(UUID.randomUUID())
        }
        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldReturnNull_whenLookingForNonexistentIdInNonEmptyFile() {
        // Given
        taskCsv.add(task)

        // When
        val result = taskCsv.getById(UUID.randomUUID())

        // Then
        assertThat(result).isNull()
    }
}
