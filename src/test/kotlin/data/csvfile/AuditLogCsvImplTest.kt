package data.csvfile

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.example.data.local.csvfile.AuditLogCsvImpl
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.utils.PlanMateException
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
    fun shouldReturnEntity_whenAddAuditLog() = runTest {
        csv.add(auditLog)
        val all = csv.get()
        assertThat(all).hasSize(1)
    }

    @Test
    fun shouldReturnEntityById_whenExists() = runTest {
        csv.add(auditLog)
        val result = csv.getById(auditLog.id)
        assertThat(result).isEqualTo(auditLog)
    }

    @Test
    fun shouldReturnNull_whenFileIsEmptyAndIdNotFound() = runTest {
        file.writeText("")
        val result = csv.getById(UUID.randomUUID())
        assertThat(result).isNull()
    }

    @Test
    fun shouldUpdateEntity_whenIdExists() = runTest {
        csv.add(auditLog)
        val updated = auditLog.copy(changeDetails = "Updated info")
        csv.update(updated)
        val result = csv.getById(updated.id)
        assertThat(result?.changeDetails).isEqualTo("Updated info")
    }

    @Test
    fun shouldThrowException_whenCsvLineIsMalformed() = runTest {
        file.writeText("invalid,line,also-invalid")
        val exception = assertFailsWith<PlanMateException.InvalidFormatException> {
            csv.get()
        }
        assertThat(exception).hasMessageThat().contains("Invalid UUID string")
    }

    @Test
    fun shouldThrowFileWriteException_whenUpdateFails() = runTest {
        csv.add(auditLog)
        val readOnlyFile = File(tempDir, "audit_logs.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)
        val failingCsv = AuditLogCsvImpl(readOnlyFile.absolutePath)
        val updatedAuditLog = auditLog.copy(changeDetails = "Updated info")
        val exception = assertFailsWith<PlanMateException.FileWriteException> {
            failingCsv.update(updatedAuditLog)
        }
        assertThat(exception).hasMessageThat().contains("Error updating audit log")
    }

    @Test
    fun shouldThrowItemNotFound_whenUpdatingNonExistentEntity() = runTest {
        val nonExistent = auditLog.copy(id = UUID.randomUUID())
        val exception = assertFailsWith<PlanMateException.ItemNotFoundException> {
            csv.update(nonExistent)
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldDeleteEntity_whenIdExists() = runTest {
        csv.add(auditLog)
        csv.delete(auditLog.id)
        assertThat(csv.get()).isEmpty()
    }

    @Test
    fun shouldReturnEmptyList_whenFileIsEmpty() = runTest {
        file.writeText("")
        val result = csv.get()
        assertThat(result).isEmpty()
    }

    @Test
    fun ensureFileExists_shouldReturn_whenFileAlreadyExists() = runTest {
        file.createNewFile()
        val csv = AuditLogCsvImpl(file.absolutePath)
        csv.add(auditLog)
        assertThat(file.exists()).isTrue()
        assertThat(csv.get()).hasSize(1)
    }

    @Test
    fun shouldThrowFileWriteException_whenFileCreationFails() = runTest {
        val nonExistentDir = File(tempDir, "non_existent_dir")
        val failingFile = File(nonExistentDir, "audit_logs.csv")
        val csv = AuditLogCsvImpl(failingFile.absolutePath)
        val exception = assertFailsWith<PlanMateException.FileWriteException> {
            csv.add(auditLog)
        }
        assertThat(exception).hasMessageThat().contains("Error creating file")
    }

    @Test
    fun shouldThrowFileWriteException_whenWriteFails() = runTest {
        val readOnlyFile = File(tempDir, "audit_logs.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)
        val failingCsv = AuditLogCsvImpl(readOnlyFile.absolutePath)
        val exception = assertFailsWith<PlanMateException.FileWriteException> {
            failingCsv.add(auditLog)
        }
        assertThat(exception).hasMessageThat().contains("Error writing to file")
    }

    @Test
    fun shouldThrowFileReadException_whenFileContentIsMalformed() = runTest {
        file.writeText("invalid,line,malformed,content")
        val exception = assertFailsWith<PlanMateException.InvalidFormatException> {
            csv.get()
        }
        assertThat(exception).hasMessageThat().contains("Malformed CSV line: invalid,line,malformed,content")
    }

    @Test
    fun shouldSuccessfullyParseFile_whenValidContent() = runTest {
        val validAuditLog = auditLog
        file.writeText("${validAuditLog.id},${validAuditLog.userId},${validAuditLog.entityType},${validAuditLog.entityId},${validAuditLog.action},${validAuditLog.changeDetails},${validAuditLog.timestamp}")
        val result = csv.get()
        assertThat(result.first()).isEqualTo(validAuditLog)
    }

    @Test
    fun shouldReturnNotEmpty_whenFileContainsEmptyLines() = runTest {
        val validAuditLog = auditLog
        file.writeText("\n\n${validAuditLog.id},${validAuditLog.userId},${validAuditLog.entityType},${validAuditLog.entityId},${validAuditLog.action},${validAuditLog.changeDetails},${validAuditLog.timestamp}\n\n")
        val result = csv.get()
        assertThat(result).isNotEmpty()
        assertThat(result.first()).isEqualTo(validAuditLog)
    }

    @Test
    fun shouldThrowItemNotFoundException_whenUpdatingNonExistentEntity() = runTest {
        val nonExistentProject = auditLog.copy(id = UUID.randomUUID())
        val exception = assertFailsWith<PlanMateException.ItemNotFoundException> {
            csv.update(nonExistentProject)
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldThrowItemNotFound_whenUpdatingNonExistentProject() = runTest {
        val nonExistentProject = auditLog.copy(id = UUID.randomUUID())
        val exception = assertFailsWith<PlanMateException.ItemNotFoundException> {
            csv.update(nonExistentProject)
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldThrowException_whenCreatedByAdminIdIsInvalidUUID() = runTest {
        file.writeText("${UUID.randomUUID()},Test,invalid-uuid,2025-04-29T15:00:00")
        val exception = assertFailsWith<PlanMateException.InvalidFormatException> {
            csv.get()
        }
        assertThat(exception).hasMessageThat().contains("Malformed CSV line")
    }

    @Test
    fun shouldThrowFileWriteException_whenDeleteFails() = runTest {
        csv.add(auditLog)
        val readOnlyFile = File(tempDir, "audit_logs.csv")
        readOnlyFile.createNewFile()
        readOnlyFile.setReadable(true)
        readOnlyFile.setWritable(false)
        val failingCsv = AuditLogCsvImpl(readOnlyFile.absolutePath)
        val exception = assertFailsWith<PlanMateException.FileWriteException> {
            failingCsv.delete(auditLog.id)
        }
        assertThat(exception).hasMessageThat().contains("Error deleting audit log")
    }

    @Test
    fun shouldThrowItemNotFound_whenDeletingNonExistentEntityInNonEmptyFile() = runTest {
        csv.add(auditLog)
        val exception = assertFailsWith<PlanMateException.ItemNotFoundException> {
            csv.delete(UUID.randomUUID())
        }
        assertThat(exception).hasMessageThat().contains("not found")
    }

    @Test
    fun shouldReturnNull_whenLookingForNonexistentIdInNonEmptyFile() = runTest {
        csv.add(auditLog)
        val result = csv.getById(UUID.randomUUID())
        assertThat(result).isNull()
    }
}