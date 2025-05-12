package logic.usecase.audit

import com.google.common.truth.Truth.assertThat
import fakeData.createAuditLogEntity
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.entity.AuditAction
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import org.example.logic.usecase.audit.GetAuditLogUseCase
import org.example.utils.PlanMateException.InvalidStateIdException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*


class GetAuditLogUseCaseTest {
    private lateinit var getAuditLogsUseCase: GetAuditLogUseCase
    private val auditLogRepository: AuditLogRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        getAuditLogsUseCase = GetAuditLogUseCase(auditLogRepository)
    }


    @Test
    fun `should retrieve audit logs filtered by project ID when provided ID is valid`() = runTest {
        // Given
        val projectUUID = "0f89e958-d40d-4b57-a8e6-75ad7ac0f679"
        val projectId = UUID.fromString(projectUUID)
        val auditLog1 = createAuditLogEntity(
            entityId = projectId,
            entityType = AuditedEntityType.PROJECT,
            action = AuditAction.CREATE,
            changeDetails = "Project created"
        )

        val auditLog2 = createAuditLogEntity(
            entityId = projectId,
            entityType = AuditedEntityType.PROJECT,
            action = AuditAction.UPDATE,
            changeDetails = "Project updated"
        )

        val expectedLogs = listOf(auditLog1, auditLog2)
        coEvery { auditLogRepository.getProjectHistory(projectId) } returns listOf(auditLog1, auditLog2)

        // When
        val result = getAuditLogsUseCase.invoke(projectId, entityType = AuditedEntityType.PROJECT)

        // Then
        assertThat(result).isEqualTo(expectedLogs)
    }

    @Test
    fun `should retrieve audit logs filtered by task ID when provided ID is valid`() = runTest {
        // Given
        val taskUUID = "0f89e958-d40d-4b57-a8e6-75ad7ac0f679"
        val taskId = UUID.fromString(taskUUID)
        val taskLog1 = createAuditLogEntity(
            entityId = taskId,
            entityType = AuditedEntityType.TASK,
            action = AuditAction.CREATE,
            changeDetails = "Task updated"
        )
        val taskLog2 = createAuditLogEntity(
            entityId = taskId,
            entityType = AuditedEntityType.TASK,
            action = AuditAction.UPDATE,
            changeDetails = "Task created"
        )

        val expectedLogs = listOf(taskLog1, taskLog2)
        coEvery { auditLogRepository.getTaskHistory(taskId) } returns expectedLogs

        // When
        val result = getAuditLogsUseCase.invoke(taskId, AuditedEntityType.TASK)

        // Then
        assertThat(result).isEqualTo(expectedLogs)
    }

    @Test
    fun `should throw InvalidStateIdException when using invalid ID`() = runTest {
        // Given
        val taskId = UUID.randomUUID()
        val exception = InvalidStateIdException()
        coEvery { auditLogRepository.getTaskHistory(taskId) } throws exception

        // When & then
        assertThrows<InvalidStateIdException> { getAuditLogsUseCase.invoke(taskId, AuditedEntityType.TASK) }
    }
}