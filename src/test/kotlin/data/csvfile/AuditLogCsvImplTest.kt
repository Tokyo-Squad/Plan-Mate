package data.csvfile

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.utils.PlanMatException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFailsWith

class AuditLogCsvImplTest {

    @TempDir
    lateinit var tempDir: File

    private lateinit var file: File
    private lateinit var csv: AuditLogCsvImpl
    private lateinit var auditLog: AuditLogEntity

    @BeforeEach
    fun setup() {
        file = File(tempDir, "audit_logs.csv")
        csv = AuditLogCsvImpl(file.absolutePath)
        auditLog = AuditLogEntity(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            entityType = AuditedEntityType.PROJECT,
            entityId = UUID.randomUUID(),
            action = AuditAction.CREATE,
            changeDetails = "Initial creation",
            timestamp = LocalDateTime.parse("2025-04-29T15:00:00")
        )
    }

    @Test
    fun shouldReturnEntity_whenAddAuditLog() {
        // When
        csv.add(auditLog)

        // Then
        val all = csv.get()
        assertThat(all).hasSize(1)
    }

    @Test
    fun shouldReturnEntityById_whenExists() {
        // Given
        csv.add(auditLog)

        // When
        val result = csv.getById(auditLog.id)

        // Then
        assertThat(result).isEqualTo(auditLog)
    }

    @Test
    fun shouldUpdateEntity_whenIdExists() {
        // Given
        csv.add(auditLog)
        val updated = auditLog.copy(changeDetails = "Updated info")

        // When
        csv.update(updated)

        // Then
        val result = csv.getById(updated.id)
        assertThat(result?.changeDetails).isEqualTo("Updated info")
    }

    @Test
    fun shouldThrowException_whenCsvLineIsMalformed() {
        // Given
        file.writeText("invalid,line,also-invalid")

        // Then
        val exception = assertFailsWith<PlanMatException.InvalidFormatException> {
            csv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun shouldThrowFileWriteException_whenUpdateFails() {
        // Given
        csv.add(auditLog)

        // Simulate a situation where the file cannot be written to
        val readOnlyFile = File(tempDir, "audit_logs.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = AuditLogCsvImpl(readOnlyFile.absolutePath)

        val updatedAuditLog = auditLog.copy(changeDetails = "Updated info")

        // When
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingCsv.update(updatedAuditLog)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error updating audit log")
    }

    @Test
    fun shouldThrowItemNotFound_whenUpdatingNonExistentEntity() {
        // Given
        val nonExistent = auditLog.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            csv.update(nonExistent)
        }
        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldDeleteEntity_whenIdExists() {
        // Given
        csv.add(auditLog)

        // When
        csv.delete(auditLog.id)

        // Then
        assertThat(csv.get()).isEmpty()
    }

    @Test
    fun shouldThrowItemNotFound_whenDeletingNonExistentEntity() {
        // When / Then
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            csv.delete(UUID.randomUUID())
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldReturnEmptyList_whenFileIsEmpty() {
        // Given
        file.writeText("")

        // When
        val result = csv.get()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun ensureFileExists_shouldReturn_whenFileAlreadyExists() {
        // Given
        file.createNewFile()
        assertThat(file.exists()).isTrue()

        val csv = AuditLogCsvImpl(file.absolutePath)

        // When
        csv.add(auditLog)

        // Then
        assertThat(file.exists()).isTrue()
        assertThat(csv.get()).hasSize(1)
    }

    @Test
    fun shouldThrowFileWriteException_whenFileCreationFails() {
        // Given
        val nonExistentDir = File(tempDir, "non_existent_dir")


        val failingFile = File(nonExistentDir, "audit_logs.csv")

        // When
        val csv = AuditLogCsvImpl(failingFile.absolutePath)

        // Then
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            csv.add(auditLog)
        }
        assertThat(exception).hasMessageThat().contains("Error creating file")
    }

    @Test
    fun shouldThrowFileWriteException_whenWriteFails() {
        // Given
        val readOnlyFile = File(tempDir, "audit_logs.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = AuditLogCsvImpl(readOnlyFile.absolutePath)

        // When
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingCsv.add(auditLog)
        }
        // Then
        assertThat(exception).hasMessageThat().contains("Error writing to file")
    }

    @Test
    fun shouldThrowFileReadException_whenFileContentIsMalformed() {
        // Given
        file.writeText("invalid,line,malformed,content")

        // When / Then
        val exception = assertFailsWith<PlanMatException.InvalidFormatException> {
            csv.get()
        }
        assertThat(exception).hasMessageThat().contains("Malformed CSV line: invalid,line,malformed,content")
    }

    @Test
    fun shouldSuccessfullyParseFile_whenValidContent() {
        // Given
        val validAuditLog = AuditLogEntity(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            entityType = AuditedEntityType.PROJECT,
            entityId = UUID.randomUUID(),
            action = AuditAction.CREATE,
            changeDetails = "Initial creation",
            timestamp = LocalDateTime.parse("2025-04-29T15:00:00")
        )
        file.writeText("${validAuditLog.id},${validAuditLog.userId},${validAuditLog.entityType},${validAuditLog.entityId},${validAuditLog.action},${validAuditLog.changeDetails},${validAuditLog.timestamp}")

        // When
        val result = csv.get()

        // Then
        assertThat(result.first()).isEqualTo(validAuditLog)
    }

    @Test
    fun shouldReturnNotEmpty_whenFileContainsEmptyLines() {
        // Given
        val validAuditLog = AuditLogEntity(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            entityType = AuditedEntityType.PROJECT,
            entityId = UUID.randomUUID(),
            action = AuditAction.CREATE,
            changeDetails = "Initial creation",
            timestamp = LocalDateTime.parse("2025-04-29T15:00:00")
        )
        file.writeText("\n\n${validAuditLog.id},${validAuditLog.userId},${validAuditLog.entityType},${validAuditLog.entityId},${validAuditLog.action},${validAuditLog.changeDetails},${validAuditLog.timestamp}\n\n") // Contains empty lines before and after the valid audit log data

        // When
        val result = csv.get()

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result.first()).isEqualTo(validAuditLog)
    }

    @Test
    fun shouldThrowItemNotFoundException_whenUpdatingNonExistentEntity() {
        // Given
        val nonExistentProject = auditLog.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            csv.update(nonExistentProject)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldThrowItemNotFound_whenUpdatingNonExistentProject() {
        // Given
        val nonExistentProject = auditLog.copy(id = UUID.randomUUID())

        // When
        val exception = assertFailsWith<PlanMatException.ItemNotFoundException> {
            csv.update(nonExistentProject)
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
            csv.get()
        }
        assertThat(exception).hasMessageThat().contains("Malformed CSV line")
    }

    @Test
    fun shouldThrowFileWriteException_whenDeleteFails() {
        // Given
        csv.add(auditLog)
        val readOnlyFile = File(tempDir, "audit_logs.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)

        val failingCsv = AuditLogCsvImpl(readOnlyFile.absolutePath)

        // When
        val exception = assertFailsWith<PlanMatException.FileWriteException> {
            failingCsv.delete(auditLog.id)
        }

        // Then
        assertThat(exception).hasMessageThat().contains("Error deleting audit log")
    }

}