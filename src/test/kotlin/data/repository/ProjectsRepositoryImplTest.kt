package data.repository

import io.mockk.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import org.example.data.DataProvider
import org.example.data.repository.ProjectRepositoryImpl
import org.example.entity.AuditLogEntity
import org.example.entity.ProjectEntity
import org.junit.jupiter.api.BeforeEach
import java.util.*
import kotlin.test.Test
import kotlin.test.assertTrue

import io.mockk.*
import org.example.entity.AuditAction
import org.example.entity.AuditedEntityType
import org.junit.jupiter.api.*
import java.time.LocalDateTime
import java.util.*
import kotlin.test.*

class ProjectRepositoryImplTest {
    private lateinit var repository: ProjectRepositoryImpl
    private val mockAuditProvider = mockk<DataProvider<AuditLogEntity>>()

    private val testProject = ProjectEntity(
        id = UUID.randomUUID(),
        name = "Test Project",
        description = "Test Description",
        createdByAdminId = UUID.randomUUID(),
        createdAt = Clock.System.now().toLocalDateTime(UTC)
    )

    private val testUserId = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8"

    @BeforeEach
    fun setup() {
        clearAllMocks()
        repository = ProjectRepositoryImpl(mockAuditProvider)
    }

    // CREATE PROJECT TESTS
    @Test
    fun `createProject should return project when successful`() {
        every { mockAuditProvider.add(any()) } just Runs

        val result = repository.createProject(testProject, testUserId)

        assertEquals(testProject, result.getOrNull())
    }

    @Test
    fun `createProject should propagate audit log errors when they occur`() {
        val error = RuntimeException("Audit log failure")
        every { mockAuditProvider.add(any()) } throws error

        val result = repository.createProject(testProject, testUserId)

        assertEquals(error, result.exceptionOrNull())
    }



    // GET PROJECT BY ID TESTS
    @Test
    fun `getProjectById should return failure when project not found`() {
        // Assuming implementation checks some data provider
        val result = repository.getProjectById(UUID.randomUUID().toString())

        assertTrue(result.isFailure)
    }

    @Test
    fun `getProjectById should return failure for invalid UUID format`() {
        val result = repository.getProjectById("invalid-uuid")

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `getAllProjects should return empty list when no projects exist`() {
        // Assuming implementation delegates to empty data provider
        val result = repository.getAllProjects()

        assertEquals(emptyList(), result.getOrNull())
    }

    // UPDATE PROJECT TESTS
    @Test
    fun `updateProject should add audit log with UPDATE action`() {
        every { mockAuditProvider.add(any()) } just Runs

        repository.updateProject(testProject, testUserId)

        verify {
            mockAuditProvider.add(
                match { it.action == AuditAction.UPDATE }
            )
        }
    }

    @Test
    fun `updateProject should return success when audit log succeeds`() {
        every { mockAuditProvider.add(any()) } just Runs

        val result = repository.updateProject(testProject, testUserId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `updateProject should return updated project on success`() {
        every { mockAuditProvider.add(any()) } just Runs

        val result = repository.updateProject(testProject, testUserId)

        assertEquals(testProject, result.getOrNull())
    }

    @Test
    fun `updateProject should log correct entity type for projects`() {
        every { mockAuditProvider.add(any()) } just Runs

        repository.updateProject(testProject, testUserId)

        verify {
            mockAuditProvider.add(
                match { it.entityType == AuditedEntityType.PROJECT }
            )
        }
    }

    // DELETE PROJECT TESTS
    @Test
    fun `deleteProject should add audit log with DELETE action`() {
        every { mockAuditProvider.add(any()) } just Runs

        repository.deleteProject(testProject.id, testUserId)

        verify {
            mockAuditProvider.add(
                match { it.action == AuditAction.DELETE }
            )
        }
    }

    @Test
    fun `deleteProject should return success when audit log succeeds`() {
        every { mockAuditProvider.add(any()) } just Runs

        val result = repository.deleteProject(testProject.id, testUserId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteProject should log correct entity ID`() {
        every { mockAuditProvider.add(any()) } just Runs

        repository.deleteProject(testProject.id, testUserId)

        verify {
            mockAuditProvider.add(
                match { it.entityId == testProject.id }
            )
        }
    }

    @Test
    fun `createProject should handle empty user ID gracefully`() {
        every { mockAuditProvider.add(any()) } just Runs

        val result = repository.createProject(testProject, "")

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `updateProject should validate project ID before processing`() {
        val invalidProject = testProject.copy(id = UUID(0, 0))

        val result = repository.updateProject(invalidProject, testUserId)

        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

}
