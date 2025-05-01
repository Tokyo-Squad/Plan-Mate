package data.repository

import io.mockk.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import org.example.data.DataProvider
import org.example.data.repository.ProjectRepositoryImpl
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.ProjectEntity
import org.example.utils.PlanMatException
import org.junit.jupiter.api.BeforeEach
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProjectRepositoryImplTest {

    private lateinit var repository: ProjectRepositoryImpl
    private val mockProjectProvider = mockk<DataProvider<ProjectEntity>>()
    private val mockAuditProvider = mockk<DataProvider<AuditLogEntity>>()

    private val testProject = ProjectEntity(
        id = UUID.randomUUID(),
        name = "Test Project",
        createdByAdminId = UUID.randomUUID(),
        createdAt = Clock.System.now().toLocalDateTime(UTC)
    )

    private val testUserId = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8"

    @BeforeEach
    fun setup() {
        clearAllMocks()
        repository = ProjectRepositoryImpl(mockProjectProvider, mockAuditProvider)
    }

    // CREATE PROJECT TESTS
    @Test
    fun `createProject should return success when project is valid`() {
        every { mockProjectProvider.add(any()) } just Runs
        every { mockAuditProvider.add(any()) } just Runs

        val result = repository.createProject(testProject, testUserId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `createProject should return project when successful`() {
        every { mockProjectProvider.add(any()) } just Runs
        every { mockAuditProvider.add(any()) } just Runs

        val result = repository.createProject(testProject, testUserId)

        assertEquals(testProject, result.getOrNull())
    }

    @Test
    fun `createProject should save project to data provider`() {
        every { mockProjectProvider.add(any()) } just Runs
        every { mockAuditProvider.add(any()) } just Runs

        repository.createProject(testProject, testUserId)

        verify { mockProjectProvider.add(testProject) }
    }

    @Test
    fun `createProject should log CREATE action`() {
        every { mockProjectProvider.add(any()) } just Runs
        every { mockAuditProvider.add(any()) } just Runs

        repository.createProject(testProject, testUserId)

        verify {
            mockAuditProvider.add(
                match { it.action == AuditAction.CREATE }
            )
        }
    }

    @Test
    fun `createProject should fail when project name is blank`() {
        val invalidProject = testProject.copy(name = "")

        val result = repository.createProject(invalidProject, testUserId)

        assertTrue(result.exceptionOrNull() is PlanMatException.ValidationException)
    }

    // UPDATE PROJECT TESTS
    @Test
    fun `updateProject should return success when update succeeds`() {
        every { mockProjectProvider.update(any()) } just Runs
        every { mockAuditProvider.add(any()) } just Runs

        val result = repository.updateProject(testProject, testUserId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `updateProject should save changes to data provider`() {
        every { mockProjectProvider.update(any()) } just Runs
        every { mockAuditProvider.add(any()) } just Runs

        repository.updateProject(testProject, testUserId)

        verify { mockProjectProvider.update(testProject) }
    }

    @Test
    fun `updateProject should log UPDATE action`() {
        every { mockProjectProvider.update(any()) } just Runs
        every { mockAuditProvider.add(any()) } just Runs

        repository.updateProject(testProject, testUserId)

        verify {
            mockAuditProvider.add(
                match { it.action == AuditAction.UPDATE }
            )
        }
    }

    // DELETE PROJECT TESTS
    @Test
    fun `deleteProject should return success when deletion succeeds`() {
        every { mockProjectProvider.getById(any()) } returns testProject
        every { mockProjectProvider.delete(any()) } just Runs
        every { mockAuditProvider.add(any()) } just Runs

        val result = repository.deleteProject(testProject.id, testUserId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteProject should remove project from data provider`() {
        every { mockProjectProvider.getById(any()) } returns testProject
        every { mockProjectProvider.delete(any()) } just Runs
        every { mockAuditProvider.add(any()) } just Runs

        repository.deleteProject(testProject.id, testUserId)

        verify { mockProjectProvider.delete(testProject.id) }
    }

    @Test
    fun `deleteProject should include project name in audit log`() {
        every { mockProjectProvider.getById(any()) } returns testProject
        every { mockProjectProvider.delete(any()) } just Runs
        every { mockAuditProvider.add(any()) } just Runs

        repository.deleteProject(testProject.id, testUserId)

        verify {
            mockAuditProvider.add(
                match { it.changeDetails.contains(testProject.name) }
            )
        }
    }

    @Test
    fun `deleteProject should fail when project not found`() {
        every { mockProjectProvider.getById(any()) } returns null

        val result = repository.deleteProject(testProject.id, testUserId)

        assertTrue(result.isFailure)
    }

    // GET PROJECT TESTS
    @Test
    fun `getProjectById should return project when found`() {
        every { mockProjectProvider.getById(any()) } returns testProject

        val result = repository.getProjectById(testProject.id.toString())

        assertEquals(testProject, result.getOrNull())
    }

    @Test
    fun `getAllProjects should return projects from data provider`() {
        val projects = listOf(testProject)
        every { mockProjectProvider.get() } returns projects

        val result = repository.getAllProjects()

        assertEquals(projects, result.getOrNull())
    }
}