package logic.usecase.audit

import com.google.common.truth.Truth.assertThat
import fakeData.createAuditLogEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.example.entity.AuditAction
import org.example.entity.AuditedEntityType
import org.example.logic.repository.AuditLogRepository
import org.example.logic.usecase.audit.AddAuditLogUseCase
import org.example.utils.PlanMateException.FileWriteException
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
    fun `should call repository to add audit log and return true on success`() = runTest{
        // Given
        val auditLogEntity = createAuditLogEntity(
            entityType = AuditedEntityType.TASK,
            action = AuditAction.CREATE,
            changeDetails = "Task created"
        )
        // When
        coEvery { auditLogRepository.addAudit(auditLogEntity) } returns Unit
        val result = addAuditLogUseCase.invoke(auditLogEntity)

        // Then
        coVerify(exactly = 1) { auditLogRepository.addAudit(auditLogEntity) }
        assertThat(result).isTrue()
    }

    @Test
    fun `should call repository and return false when FileWriteException is thrown by repository`() = runTest{
        // Given
        val auditLogEntity = createAuditLogEntity(
            entityType = AuditedEntityType.PROJECT,
            action = AuditAction.UPDATE,
            changeDetails = "Project updated"
        )
        val exception = FileWriteException("Error writing audit log to file.")
        coEvery { auditLogRepository.addAudit(auditLogEntity) } throws exception

        // When
        val result = addAuditLogUseCase.invoke(auditLogEntity)

        // Then
        coVerify(exactly = 1) { auditLogRepository.addAudit(auditLogEntity) }
        assertThat(result).isFalse()
    }
}