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
            id = UUID.randomUUID(),
            name = "Initial State",
            projectId = UUID.randomUUID()
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
    fun shouldThrowItemNotFound_whenDeletingNonExistentEntity() {
        // When / Then
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            stateCsv.delete(UUID.randomUUID())
        }
        assertThat(exception).hasMessageThat().contains("not found")
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
}