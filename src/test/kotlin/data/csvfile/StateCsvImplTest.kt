package data.csvfile

import com.google.common.truth.Truth.assertThat
import org.example.entity.StateEntity
import org.example.utils.PlanMatException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFailsWith

class StateCsvImplTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var file: File
    private lateinit var stateCsv: StateCsvImpl
    private lateinit var state: StateEntity

    @BeforeEach
    fun setup() {
        file = File(tempDir, "states.csv")
        stateCsv = StateCsvImpl(file.absolutePath)
        state = StateEntity(
            id = UUID.randomUUID(), name = "Initial State", projectId = UUID.randomUUID()
        )
    }

    @Test
    fun shouldReturnEntity_whenAddState() {
        // When
        stateCsv.add(state)

        // Then
        val all = stateCsv.get()
        assertThat(all).hasSize(1)
    }

    @Test
    fun shouldReturnEntityById_whenExists() {
        // Given
        stateCsv.add(state)

        // When
        val result = stateCsv.getById(state.id)

        // Then
        assertThat(result).isEqualTo(state)
    }

    @Test
    fun shouldReturnNull_whenFileIsEmptyAndIdNotFound() {
        // Given
        file.writeText("")

        // When
        val result = stateCsv.getById(UUID.randomUUID())

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun shouldThrowException_whenCsvLineIsMalformed() {
        // Given
        file.writeText("invalid,line,also-invalid")

        // Then
        val exception = assertFailsWith<PlanMatException.InvalidFormatException> {
            stateCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun shouldUpdateEntity_whenIdExists() {
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
    fun shouldThrowItemNotFound_whenUpdatingNonExistentEntity() {
        // Given
        val nonExistent = state.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            stateCsv.update(nonExistent)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldDeleteEntity_whenIdExists() {
        // Given
        stateCsv.add(state)

        // When
        stateCsv.delete(state.id)

        // Then
        assertThat(stateCsv.get()).isEmpty()
    }

    @Test
    fun shouldReturnEmptyList_whenFileIsEmpty() {
        // Given
        file.writeText("")

        // When
        val result = stateCsv.get()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun shouldThrowFileWriteException_whenFileCreationFails() {
        // Given
        val nonExistentDir = File(tempDir, "non_existent_dir")
        val failingFile = File(nonExistentDir, "states.csv")

        // When
        val failingCsv = StateCsvImpl(failingFile.absolutePath)

        // Then
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingCsv.add(state)
        }
        assertThat(exception).hasMessageThat().contains("Error creating file")
    }

    @Test
    fun shouldThrowFileWriteException_whenWriteFails() {
        // Given
        val readOnlyFile = File(tempDir, "states.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = StateCsvImpl(readOnlyFile.absolutePath)

        // When
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingCsv.add(state)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error writing to file")
    }

    @Test
    fun ensureFileExists_shouldReturn_whenFileAlreadyExists() {
        // Given
        file.createNewFile()
        assertThat(file.exists()).isTrue()

        val stateCsv = StateCsvImpl(file.absolutePath)

        // When
        stateCsv.add(state)

        // Then
        assertThat(file.exists()).isTrue()
    }

    @Test
    fun shouldThrowFileReadException_whenFileContentIsMalformed() {
        // Given
        file.writeText("invalid,line,malformed,content")

        // When / Then
        val exception = assertFailsWith<PlanMatException.InvalidFormatException> {
            stateCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun shouldSuccessfullyParseFile_whenValidContent() {
        // Given
        val validState = StateEntity(
            id = UUID.randomUUID(), name = "Valid State", projectId = UUID.randomUUID()
        )
        file.writeText("${validState.id},${validState.name},${validState.projectId}")

        // When
        val result = stateCsv.get()

        // Then
        assertThat(result.first()).isEqualTo(validState)
    }

    @Test
    fun shouldReturnNotEmpty_whenFileContainsEmptyLines() {
        // Given
        val validState = StateEntity(
            id = UUID.randomUUID(), name = "Valid State", projectId = UUID.randomUUID()
        )
        file.writeText("\n\n${validState.id},${validState.name},${validState.projectId}\n\n") // Contains empty lines before and after the valid state data

        // When
        val result = stateCsv.get()

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result.first()).isEqualTo(validState)
    }

    @Test
    fun shouldThrowItemNotFoundException_whenUpdatingNonExistentEntity() {
        // Given
        val nonExistentState = state.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            stateCsv.update(nonExistentState)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldThrowItemNotFound_whenUpdatingNonExistentState() {
        // Given
        val nonExistentState = state.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            stateCsv.update(nonExistentState)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldThrowException_whenCreatedByAdminIdIsInvalidUUID() {
        // Given
        file.writeText("${UUID.randomUUID()},Test,invalid-uuid,2025-04-29T15:00:00")

        // When / Then
        val exception = assertFailsWith<PlanMatException.InvalidFormatException> {
            stateCsv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun shouldThrowFileWriteException_whenDeleteFails() {
        // Given
        stateCsv.add(state)
        val readOnlyFile = File(tempDir, "states.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = StateCsvImpl(readOnlyFile.absolutePath)

        // When
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingCsv.delete(state.id)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error deleting state")
    }

    @Test
    fun shouldThrowFileWriteException_whenUpdateFails() {
        // Given
        stateCsv.add(state)
        val readOnlyFile = File(tempDir, "states.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = StateCsvImpl(readOnlyFile.absolutePath)

        val updatedState = state.copy(name = "Updated State Name")

        // When
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingCsv.update(updatedState)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error updating state")
    }

    @Test
    fun shouldThrowItemNotFound_whenDeletingNonExistentEntityInNonEmptyFile() {
        // Given
        stateCsv.add(state)

        // When
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            stateCsv.delete(UUID.randomUUID())
        }
        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldReturnNull_whenLookingForNonexistentIdInNonEmptyFile() {
        // Given
        stateCsv.add(state)

        // When
        val result = stateCsv.getById(UUID.randomUUID())

        // Then
        assertThat(result).isNull()
    }
}