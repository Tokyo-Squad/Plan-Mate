package data.repository

import fakeData.createAuditLogEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.data.DataProvider
import org.example.data.repository.AuditLogRepositoryImpl
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.UUID
import kotlin.test.Test


class AuditLogRepositoryImplTest {
    private lateinit var auditLogRepository: AuditLogRepositoryImpl
    private val dataProvider: DataProvider<AuditLogEntity> = mockk()

    @BeforeEach
    fun setUp() {
        auditLogRepository = AuditLogRepositoryImpl(dataProvider)
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
        auditLogRepository.addAudit(auditLogEntity)

        // Then
        verify(exactly = 1) { dataProvider.add(auditLogEntity) }
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

        val auditLog3 = createAuditLogEntity(
            entityId = UUID.randomUUID(),
            entityType = AuditedEntityType.PROJECT,
            action = AuditAction.CREATE,
            changeDetails = "Another Project created"
        )

        every { dataProvider.get() } returns listOf(auditLog1, auditLog2,auditLog3)

        // When
        val projectHistory = auditLogRepository.getProjectHistory(projectId)

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
        val taskLog3 = createAuditLogEntity(
            entityId = UUID.randomUUID(),
            entityType = AuditedEntityType.TASK,
            action = AuditAction.CREATE,
            changeDetails = "Another Task Created"
        )
        every { dataProvider.get() } returns listOf(taskLog1, taskLog2, taskLog3)

        // When
        val taskHistory = auditLogRepository.getTaskHistory(taskId)

        // Then
        assertEquals(listOf(taskLog1, taskLog2), taskHistory)
    }

    @Test
    fun `should not allow saving a log with missing mandatory fields`() {
        TODO("Implementation of test case")
    }

    @Test
    fun `should throw exception when project ID is not found`() {
        // Given
        val nonExistentProjectId = 999
        every { dataProvider.get() } returns listOf(
            createAuditLogEntity(entityId = UUID.nameUUIDFromBytes("123".toByteArray()), entityType = AuditedEntityType.PROJECT),
            createAuditLogEntity(entityId = UUID.nameUUIDFromBytes("456".toByteArray()), entityType = AuditedEntityType.TASK)
        )

        // When & Then
        assertThrows(NoSuchElementException::class.java) {
            auditLogRepository.getProjectHistory(nonExistentProjectId)
        }
    }

    @Test
    fun `should throw exception when task ID is not found`() {
        // Given
        val nonExistentTaskId = 999
        every { dataProvider.get() } returns listOf(
            createAuditLogEntity(entityId = UUID.nameUUIDFromBytes("123".toByteArray()), entityType = AuditedEntityType.PROJECT),
            createAuditLogEntity(entityId = UUID.nameUUIDFromBytes("456".toByteArray()), entityType = AuditedEntityType.TASK)
        )

        // When & Then
        assertThrows(NoSuchElementException::class.java) {
            auditLogRepository.getTaskHistory(nonExistentTaskId)
        }
    }

    @Test
    fun `should throw exception when audit log is empty`() {
        // Given
        val nonExistentTaskId = 999
        every { dataProvider.get() } returns emptyList()

        // When && Then
        assertThrows(NoSuchElementException::class.java){
            auditLogRepository.getTaskHistory(nonExistentTaskId)
        }
    }

}