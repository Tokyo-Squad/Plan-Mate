package logic.usecase

import fakeData.createAuditLogEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.AuditAction
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import org.example.logic.usecase.AuditLogUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.UUID
import kotlin.test.Test

class AuditLogUseCaseTest {

    private lateinit var auditLogUseCase: AuditLogUseCase
    private val auditLogRepository: AuditLogRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        auditLogUseCase = AuditLogUseCase(auditLogRepository)
    }

    @Test
    fun `should create a new audit log when creating a new project or task`() {
        // Given
        val auditLogEntity = createAuditLogEntity(
            entityType = AuditedEntityType.TASK,
            action = AuditAction.CREATE,
            changeDetails = "Task created"
        )

        // When
        auditLogUseCase.addAuditLog(auditLogEntity)

        // Then
        verify(exactly = 1) { auditLogRepository.addAudit(auditLogEntity) }
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


        every { auditLogRepository.getProjectHistory(projectId) } returns listOf(auditLog1, auditLog2)

        // When
        val projectHistory = auditLogUseCase.getProjectAuditLogs(projectId)

        // Then
        assertEquals(listOf(auditLog1, auditLog2), projectHistory)
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

        every { auditLogRepository.getTaskHistory(taskId) } returns listOf(taskLog1, taskLog2, )

        // When
        val taskHistory = auditLogUseCase.getTaskAuditLogs(taskId)

        // Then
        assertEquals(listOf(taskLog1, taskLog2), taskHistory)
    }


}