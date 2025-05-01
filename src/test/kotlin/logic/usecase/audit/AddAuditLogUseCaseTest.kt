package logic.usecase.audit

import fakeData.createAuditLogEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.AuditAction
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import org.example.logic.usecase.audit.AddAuditLogUseCase
import org.example.utils.PlanMatException.InvalidStateIdException
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddAuditLogUseCaseTest {
    private lateinit var addAuditLogUseCase: AddAuditLogUseCase
    private val auditLogRepository: AuditLogRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        addAuditLogUseCase = AddAuditLogUseCase(auditLogRepository)
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
        addAuditLogUseCase.invoke(auditLogEntity)

        // Then
        verify(exactly = 1) { auditLogRepository.addAudit(auditLogEntity) }
    }

    @Test
    fun `should return failure when InvalidStateIdException is thrown by repository`() {
        // Given
        val auditLogEntity = createAuditLogEntity(
            entityType = AuditedEntityType.PROJECT,
            action = AuditAction.UPDATE,
            changeDetails = "Project updated"
        )
        val exception = InvalidStateIdException("Failed to add audit log due to invalid state.")
        every { auditLogRepository.addAudit(auditLogEntity) } throws exception

        // When
        val result = addAuditLogUseCase.invoke(auditLogEntity)

        // Then
        verify(exactly = 1) { auditLogRepository.addAudit(auditLogEntity) }
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InvalidStateIdException)
        assertTrue(result.exceptionOrNull()?.message == "Failed to add audit log due to invalid state.")
    }

}