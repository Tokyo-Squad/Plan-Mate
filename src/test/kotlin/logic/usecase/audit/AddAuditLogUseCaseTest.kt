package logic.usecase.audit

import fakeData.createAuditLogEntity
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.AuditAction
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import org.example.logic.usecase.audit.AddAuditLogUseCase
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
}