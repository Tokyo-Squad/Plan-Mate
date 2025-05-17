package data.csvfile

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.example.data.local.csvfile.WorkflowStateCsvImpl
import org.example.data.util.exception.FileException
import logic.model.WorkflowState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertFailsWith

class WorkflowWorkflowStateCsvImplTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var file: File
    private lateinit var stateCsv: WorkflowStateCsvImpl
    private lateinit var state: WorkflowState

    @BeforeEach
    fun setup() {
        file = File(tempDir, "states.csv")
        stateCsv = WorkflowStateCsvImpl(file.absolutePath)
        state = WorkflowState(
            id = UUID.randomUUID(), name = "Initial State", projectId = UUID.randomUUID()
        )
    }

    @Test
    fun `should return entity when add state`() = runTest {
        // When
        stateCsv.add(state)

        // Then
        val all = stateCsv.get()
        assertThat(all).hasSize(1)
    }

    @Test
    fun `should return entity by id when exists`() = runTest {
        // Given
        stateCsv.add(state)

        // When
        val result = stateCsv.getById(state.id)

        // Then
        assertThat(result).isEqualTo(state)
    }

    @Test
    fun `should return null when file is empty and id not found`() = runTest {
        // Given
        file.writeText("")

        // When
        val result = stateCsv.getById(UUID.randomUUID())

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `should throw exception when csv line is malformed`() = runTest {
        // Given
        file.writeText("invalid,line,also-invalid")

        // Then
        val exception = assertFailsWith<FileException.FileInvalidFormatException> {
            stateCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun `should update entity when id exists`() = runTest {
        // Given
        stateCsv.add(state)
        val updated = state.copy(name = "Updated State")

        // When
        stateCsv.update(updated)

        // Then
        val result = stateCsv.getById(updated.id)
        assertThat(result?.name).isEqualTo("Updated State")
    }

    @Test
    fun `should throw item not found when updating non-existent entity`() = runTest {
        // Given
        val nonExistent = state.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<FileException.FileItemNotFoundException> {
            stateCsv.update(nonExistent)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun `should delete entity when id exists`() = runTest {
        // Given
        stateCsv.add(state)

        // When
        stateCsv.delete(state.id)

        // Then
        assertThat(stateCsv.get()).isEmpty()
    }

    @Test
    fun `should return empty list when file is empty`() = runTest {
        // Given
        file.writeText("")

        // When
        val result = stateCsv.get()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should throw file write exception when file creation fails`() = runTest {
        // Given
        val nonExistentDir = File(tempDir, "non_existent_dir")
        val failingFile = File(nonExistentDir, "states.csv")

        // When
        val failingCsv = WorkflowStateCsvImpl(failingFile.absolutePath)

        // Then
        val exception = assertFailsWith<FileException.FileWriteException> {
            failingCsv.add(state)
        }
        assertThat(exception).hasMessageThat().contains("Error creating file")
    }

    @Test
    fun `should throw file write exception when write fails`() = runTest {
        // Given
        val readOnlyFile = File(tempDir, "states.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = WorkflowStateCsvImpl(readOnlyFile.absolutePath)

        // When
        val exception = assertFailsWith<FileException.FileWriteException> {
            failingCsv.add(state)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error writing to file")
    }

    @Test
    fun `ensure file exists should return when file already exists`() = runTest {
        // Given
        file.createNewFile()
        assertThat(file.exists()).isTrue()

        val stateCsv = WorkflowStateCsvImpl(file.absolutePath)

        // When
        stateCsv.add(state)

        // Then
        assertThat(file.exists()).isTrue()
    }

    @Test
    fun `should throw file read exception when file content is malformed`() = runTest {
        // Given
        file.writeText("invalid,line,malformed,content")

        // When / Then
        val exception = assertFailsWith<FileException.FileInvalidFormatException> {
            stateCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun `should successfully parse file when valid content`() = runTest {
        // Given
        val validWorkflowState = WorkflowState(
            id = UUID.randomUUID(), name = "Valid State", projectId = UUID.randomUUID()
        )
        file.writeText("${validWorkflowState.id},${validWorkflowState.name},${validWorkflowState.projectId}")

        // When
        val result = stateCsv.get()

        // Then
        assertThat(result.first()).isEqualTo(validWorkflowState)
    }

    @Test
    fun `should return not empty when file contains empty lines`() = runTest {
        // Given
        val validWorkflowState = WorkflowState(
            id = UUID.randomUUID(), name = "Valid State", projectId = UUID.randomUUID()
        )
        file.writeText("\n\n${validWorkflowState.id},${validWorkflowState.name},${validWorkflowState.projectId}\n\n") // Contains empty lines before and after the valid state data

        // When
        val result = stateCsv.get()

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result.first()).isEqualTo(validWorkflowState)
    }

    @Test
    fun `should throw item not found exception when updating non-existent entity`() = runTest {
        // Given
        val nonExistentState = state.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<FileException.FileItemNotFoundException> {
            stateCsv.update(nonExistentState)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun `should throw item not found when updating non-existent state`() = runTest {
        // Given
        val nonExistentState = state.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<FileException.FileItemNotFoundException> {
            stateCsv.update(nonExistentState)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun `should throw exception when created by admin id is invalid UUID`() = runTest {
        // Given
        file.writeText("${UUID.randomUUID()},Test,invalid-uuid,2025-04-29T15:00:00")

        // When / Then
        val exception = assertFailsWith<FileException.FileInvalidFormatException> {
            stateCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun `should throw file write exception when delete fails`() = runTest {
        // Given
        stateCsv.add(state)
        val readOnlyFile = File(tempDir, "states.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = WorkflowStateCsvImpl(readOnlyFile.absolutePath)

        // When
        val exception = assertFailsWith<FileException.FileWriteException> {
            failingCsv.delete(state.id)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error deleting state")
    }

    @Test
    fun `should throw file write exception when update fails`() = runTest {
        // Given
        stateCsv.add(state)
        val readOnlyFile = File(tempDir, "states.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = WorkflowStateCsvImpl(readOnlyFile.absolutePath)

        val updatedState = state.copy(name = "Updated State Name")

        // When
        val exception = assertFailsWith<FileException.FileWriteException> {
            failingCsv.update(updatedState)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error updating state")
    }

    @Test
    fun `should throw item not found when deleting non-existent entity in non-empty file`() = runTest {
        // Given
        stateCsv.add(state)

        // When
        val exception = assertFailsWith<FileException.FileItemNotFoundException> {
            stateCsv.delete(UUID.randomUUID())
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun `should return null when looking for nonexistent id in non-empty file`() = runTest {
        // Given
        stateCsv.add(state)

        // When
        val result = stateCsv.getById(UUID.randomUUID())

        // Then
        assertThat(result).isNull()
    }
}