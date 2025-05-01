package logic.usecase.audit

import com.google.common.truth.Truth.assertThat
import fakeData.createAuditLogEntity
import io.mockk.every
import io.mockk.mockk
import org.example.entity.AuditAction
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import org.example.logic.usecase.audit.GetAuditLogUseCase
import org.example.utils.PlanMatException.InvalidStateIdException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class GetAuditLogUseCaseTest {
    private lateinit var getAuditLogsUseCase: GetAuditLogUseCase
    private val auditLogRepository: AuditLogRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        getAuditLogsUseCase = GetAuditLogUseCase(auditLogRepository)
    }


    @Test
    fun `should retrieve audit logs filtered by project ID when provided ID is valid`() {
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
        Assertions.assertTrue(result.isSuccess)
        Assertions.assertEquals(expectedLogs, result.getOrNull())
    }

    @Test
    fun `should return failure when an exception occurs while retrieving project audit logs`() {
        // Given
        val projectId = 456
        val exception = InvalidStateIdException()
        every { auditLogRepository.getProjectHistory(projectId) } throws exception

        // When
        val result = getAuditLogsUseCase.invoke(projectId, AuditedEntityType.PROJECT)

        // Then
        Assertions.assertTrue(result.isFailure)
        Assertions.assertEquals(exception, result.exceptionOrNull())

    }

    @Test
    fun `should retrieve audit logs filtered by task ID when provided ID is valid`() {
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
        val result = getAuditLogsUseCase.invoke(taskId, AuditedEntityType.TASK)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertEquals(expectedLogs, result.getOrNull())
    }

    @Test
    fun `should throw InvalidStateIdException when using invalid ID`() {
        // Given
        val taskId = 303
        val exception = InvalidStateIdException()
        every { auditLogRepository.getTaskHistory(taskId) } throws exception

        // When
        val result = getAuditLogsUseCase.invoke(taskId, AuditedEntityType.TASK)

        // Then
        assertThat(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}