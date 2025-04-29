package data.csvfile

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.example.entity.ProjectEntity
import org.example.utils.PlanMatException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFailsWith

class ProjectCsvImplTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var file: File
    private lateinit var projectCsv: ProjectCsvImpl
    private lateinit var project: ProjectEntity

    @BeforeEach
    fun setup() {
        file = File(tempDir, "projects.csv")
        projectCsv = ProjectCsvImpl(file.absolutePath)
        project = ProjectEntity(
            id = UUID.randomUUID(),
            name = "Test Project",
            createdByAdminId = UUID.randomUUID(),
            createdAt = LocalDateTime.parse("2025-04-29T15:00:00")
        )
    }

    @Test
    fun shouldReturnEntity_whenAddProject() {
        // When
        projectCsv.add(project)

        // Then
        val all = projectCsv.get()
        assertThat(all).hasSize(1)
    }

    @Test
    fun shouldReturnEntityById_whenExists() {
        // Given
        projectCsv.add(project)

        // When
        val result = projectCsv.getById(project.id)

        // Then
        assertThat(result).isEqualTo(project)
    }

    @Test
    fun shouldUpdateEntity_whenIdExists() {
        // Given
        projectCsv.add(project)
        val updated = project.copy(name = "Updated Project Name")

        // When
        projectCsv.update(updated)

        // Then
        val result = projectCsv.getById(updated.id)
        assertThat(result?.name).isEqualTo("Updated Project Name")
    }

    @Test
    fun shouldThrowItemNotFound_whenUpdatingNonExistentEntity() {
        // Given
        val nonExistent = project.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            projectCsv.update(nonExistent)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldDeleteEntity_whenIdExists() {
        // Given
        projectCsv.add(project)

        // When
        projectCsv.delete(project.id)

        // Then
        assertThat(projectCsv.get()).isEmpty()
    }

    @Test
    fun shouldThrowItemNotFound_whenDeletingNonExistentEntity() {
        // When / Then
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            projectCsv.delete(UUID.randomUUID())
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldReturnEmptyList_whenFileIsEmpty() {
        // Given
        file.writeText("")

        // When
        val result = projectCsv.get()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun shouldThrowFileWriteException_whenFileCreationFails() {
        // Given
        val nonExistentDir = File(tempDir, "non_existent_dir")
        val failingFile = File(nonExistentDir, "projects.csv")

        // When
        val failingCsv = ProjectCsvImpl(failingFile.absolutePath)

        // Then
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingCsv.add(project)
        }
        assertThat(exception).hasMessageThat().contains("Error creating file")
    }

    @Test
    fun shouldThrowFileWriteException_whenWriteFails() {
        // Given
        val readOnlyFile = File(tempDir, "projects.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = ProjectCsvImpl(readOnlyFile.absolutePath)

        // When
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingCsv.add(project)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error writing to file")
    }
}