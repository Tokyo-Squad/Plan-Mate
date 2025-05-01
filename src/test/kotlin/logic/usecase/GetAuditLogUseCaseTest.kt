package logic.usecase

import fakeData.createAuditLogEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.AuditAction
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import org.example.logic.usecase.AddAuditLogUseCase
import org.example.logic.usecase.GetAuditLogUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class GetAuditLogUseCaseTest {
    private lateinit var getAuditLogsUseCase: GetAuditLogUseCase
    private val auditLogRepository: AuditLogRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        getAuditLogsUseCase = GetAuditLogUseCase(auditLogRepository)
    }


    @Test
    fun `should retrieve audit logs filtered by project ID`() {
        // Given
        val projectId = 123
        val projectUUID = UUID.nameUUIDFromBytes(projectId.toString().toByteArray())

        val auditLog1 = createAuditLogEntity(
            entityId = projectUUID,
            entityType = AuditedEntityType.PROJECT,
            action = AuditAction.CREATE,
            changeDetails = "Project created"
        )

        val auditLog2 = createAuditLogEntity(
            entityId = projectUUID,
            entityType = AuditedEntityType.PROJECT,
            action = AuditAction.UPDATE,
            changeDetails = "Project updated"
        )

        val expectedLogs = listOf(auditLog1, auditLog2)
        every { auditLogRepository.getProjectHistory(projectId) } returns listOf(auditLog1, auditLog2)

        // When
        val result = getAuditLogsUseCase.invoke(projectId, entityType = AuditedEntityType.PROJECT)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedLogs, result.getOrNull())
    }

    @Test
    fun `should return failure when an exception occurs while retrieving project audit logs`() {
        // Given
        val projectId = 456
        val exception = NoSuchElementException("No audit logs found for ID: $projectId")
        every { auditLogRepository.getProjectHistory(projectId) } throws exception

        // When
        val result = getAuditLogsUseCase.invoke(projectId, AuditedEntityType.PROJECT)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

    }

    @Test
    fun `should retrieve audit logs filtered by task ID`() {
        // Given
        val taskId = 456
        val taskUUID = UUID.nameUUIDFromBytes(taskId.toString().toByteArray())
        val taskLog1 = createAuditLogEntity(
            entityId = taskUUID,
            entityType = AuditedEntityType.TASK,
            action = AuditAction.CREATE,
            changeDetails = "Task updated"
        )
        val taskLog2 = createAuditLogEntity(
            entityId = taskUUID,
            entityType = AuditedEntityType.TASK,
            action = AuditAction.UPDATE,
            changeDetails = "Task created"
        )

        val expectedLogs = listOf(taskLog1, taskLog2)
        every { auditLogRepository.getTaskHistory(taskId) } returns expectedLogs

        // When
        val result = getAuditLogsUseCase.invoke(taskId,AuditedEntityType.TASK)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedLogs, result.getOrNull())
    }

    @Test
    fun `should return failure when an exception occurs while retrieving task audit logs`() {
        // Given
        val taskId = 303
        val exception = NoSuchElementException("No audit logs found for ID: $taskId")
        every { auditLogRepository.getTaskHistory(taskId) } throws exception

        // When
        val result = getAuditLogsUseCase.invoke(taskId,AuditedEntityType.TASK)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}