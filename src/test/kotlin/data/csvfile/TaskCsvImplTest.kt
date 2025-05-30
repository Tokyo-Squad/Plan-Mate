package data.csvfile

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.example.data.local.csvfile.TaskCsvImpl
import org.example.data.util.exception.FileException
import domain.model.Task
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TaskCsvImplTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var file: File
    private lateinit var taskCsv: TaskCsvImpl
    private lateinit var task: Task

    @BeforeEach
    fun setup() {
        file = File(tempDir, "tasks.csv")
        taskCsv = TaskCsvImpl(file.absolutePath)
        task = Task(
            id = UUID.randomUUID(),
            title = "Initial Task",
            description = "Task description",
            workflowStateId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            createdByUserId = UUID.randomUUID(),
            createdAt = LocalDateTime.parse("2025-04-29T15:00:00")
        )
    }

    @Test
    fun shouldReturnEntity_whenAddTask() = runTest {
        taskCsv.add(task)
        val all = taskCsv.get()
        assertThat(all).hasSize(1)
    }

    @Test
    fun shouldReturnEntityById_whenExists() = runTest {
        taskCsv.add(task)
        val result = taskCsv.getById(task.id)
        assertThat(result).isEqualTo(task)
    }

    @Test
    fun shouldUpdateEntity_whenIdExists() = runTest {
        taskCsv.add(task)
        val updated = task.copy(title = "Updated Task")
        taskCsv.update(updated)
        val result = taskCsv.getById(updated.id)
        assertThat(result?.title).isEqualTo("Updated Task")
    }

    @Test
    fun shouldReturnNull_whenFileIsEmptyAndIdNotFound() = runTest {
        file.writeText("")
        val result = taskCsv.getById(UUID.randomUUID())
        assertThat(result).isNull()
    }

    @Test
    fun shouldThrowItemNotFound_whenUpdatingNonExistentEntity() = runTest {
        val nonExistent = task.copy(id = UUID.randomUUID())
        val exception = assertFailsWith<FileException.FileItemNotFoundException> {
            taskCsv.update(nonExistent)
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun ensureFileExists_shouldReturn_whenFileAlreadyExists() = runTest {
        file.createNewFile()
        assertThat(file.exists()).isTrue()
        val taskCsv = TaskCsvImpl(file.absolutePath)
        taskCsv.add(task)
        assertThat(file.exists()).isTrue()
    }

    @Test
    fun shouldDeleteEntity_whenIdExists() = runTest {
        taskCsv.add(task)
        taskCsv.delete(task.id)
        assertThat(taskCsv.get()).isEmpty()
    }

    @Test
    fun shouldReturnEmptyList_whenFileIsEmpty() = runTest {
        file.writeText("")
        val result = taskCsv.get()
        assertThat(result).isEmpty()
    }

    @Test
    fun shouldThrowFileWriteException_whenFileCreationFails() = runTest {
        val nonExistentDir = File(tempDir, "non_existent_dir")
        val failingFile = File(nonExistentDir, "tasks.csv")
        val failingCsv = TaskCsvImpl(failingFile.absolutePath)
        val exception = assertFailsWith<FileException.FileWriteException> {
            failingCsv.add(task)
        }
        assertThat(exception).hasMessageThat().contains("Error creating file")
    }

    @Test
    fun shouldThrowException_whenCsvLineIsMalformed() = runTest {
        file.writeText("invalid,line,also-invalid")
        val exception = assertFailsWith<FileException.FileInvalidFormatException> {
            taskCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun shouldThrowFileWriteException_whenWriteFails() = runTest {
        val readOnlyFile = File(tempDir, "tasks.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)
        val failingCsv = TaskCsvImpl(readOnlyFile.absolutePath)
        val exception = assertFailsWith<FileException.FileWriteException> {
            failingCsv.add(task)
        }
        assertThat(exception).hasMessageThat().contains("Error writing to file")
    }

    @Test
    fun shouldThrowFileReadException_whenFileContentIsMalformed() = runTest {
        file.writeText("invalid,line,malformed,content")
        val exception = assertFailsWith<FileException.FileInvalidFormatException> {
            taskCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun shouldSuccessfullyParseFile_whenValidContent() = runTest {
        val validTask = Task(
            id = UUID.randomUUID(),
            title = "Valid Task",
            description = "Valid Task Description",
            workflowStateId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            createdByUserId = UUID.randomUUID(),
            createdAt = LocalDateTime.parse("2025-04-29T15:00:00")
        )
        file.writeText("${validTask.id},${validTask.title},${validTask.description},${validTask.workflowStateId},${validTask.projectId},${validTask.createdByUserId},${validTask.createdAt}")
        val result = taskCsv.get()
        assertThat(result.first()).isEqualTo(validTask)
    }

    @Test
    fun shouldReturnNotEmpty_whenFileContainsEmptyLines() = runTest {
        val validTask = Task(
            id = UUID.randomUUID(),
            title = "Valid Task",
            description = "Task description",
            workflowStateId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            createdByUserId = UUID.randomUUID(),
            createdAt = LocalDateTime.parse("2025-04-29T15:00:00")
        )
        file.writeText("\n\n${validTask.id},${validTask.title},${validTask.description},${validTask.workflowStateId},${validTask.projectId},${validTask.createdByUserId},${validTask.createdAt}\n\n")
        val result = taskCsv.get()
        assertThat(result).isNotEmpty()
        assertThat(result.first()).isEqualTo(validTask)
    }

    @Test
    fun shouldThrowItemNotFoundException_whenUpdatingNonExistentEntity() = runTest {
        val nonExistentTask = task.copy(id = UUID.randomUUID())
        val exception = assertFailsWith<FileException.FileItemNotFoundException> {
            taskCsv.update(nonExistentTask)
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldThrowException_whenCreatedByAdminIdIsInvalidUUID() = runTest {
        file.writeText("${UUID.randomUUID()},Test,invalid-uuid,2025-04-29T15:00:00")
        val exception = assertFailsWith<FileException.FileInvalidFormatException> {
            taskCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun shouldThrowFileWriteException_whenDeleteFails() = runTest {
        taskCsv.add(task)
        val readOnlyFile = File(tempDir, "tasks.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)
        val failingCsv = TaskCsvImpl(readOnlyFile.absolutePath)
        val exception = assertFailsWith<FileException.FileWriteException> {
            failingCsv.delete(task.id)
        }
        assertThat(exception).hasMessageThat().contains("Error deleting task")
    }

    @Test
    fun shouldThrowFileWriteException_whenUpdateFails() = runTest {
        taskCsv.add(task)
        val readOnlyFile = File(tempDir, "tasks.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)
        val failingCsv = TaskCsvImpl(readOnlyFile.absolutePath)
        val updatedTask = task.copy(title = "Updated Task Title")
        val exception = assertFailsWith<FileException.FileWriteException> {
            failingCsv.update(updatedTask)
        }
        assertThat(exception).hasMessageThat().contains("Error updating task")
    }

    @Test
    fun shouldThrowItemNotFound_whenDeletingNonExistentEntityInNonEmptyFile() = runTest {
        taskCsv.add(task)
        val exception = assertFailsWith<FileException.FileItemNotFoundException> {
            taskCsv.delete(UUID.randomUUID())
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldReturnNull_whenLookingForNonexistentIdInNonEmptyFile() = runTest {
        taskCsv.add(task)
        val result = taskCsv.getById(UUID.randomUUID())
        assertThat(result).isNull()
    }
}