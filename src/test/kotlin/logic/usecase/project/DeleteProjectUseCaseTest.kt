package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.entity.ProjectEntity
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.project.DeleteProjectUseCase
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.Test

class DeleteProjectUseCaseTest {
    private val mockRepo = mockk<ProjectRepository>()
    private val useCase = DeleteProjectUseCase(mockRepo)
    private val testProjectId = UUID.randomUUID()
    private val testUser = UUID.randomUUID()
    private val testProject = mockk<ProjectEntity>()

    @Test
    fun `should complete successfully when deletion succeeds`() = runTest {
        coEvery { mockRepo.getProjectById(testProjectId.toString()) } returns testProject
        coEvery { mockRepo.deleteProject(testProjectId, testUser) } just Runs

        useCase(testProjectId, testUser) // Should not throw

        coVerify { mockRepo.deleteProject(testProjectId, testUser) }
    }

    @Test
    fun `should throw NoSuchElementException when project not found`() = runTest {
        coEvery { mockRepo.getProjectById(any()) } throws NoSuchElementException("Project not found")

        val exception = assertThrows<NoSuchElementException> {
            useCase(testProjectId, testUser)
        }

        assertThat(exception).hasMessageThat().contains("Project not found")
    }

    @Test
    fun `should propagate repository exceptions`() = runTest {
        val expectedError = RuntimeException("Database error")
        coEvery { mockRepo.getProjectById(any()) } throws expectedError

        val exception = assertThrows<RuntimeException> {
            useCase(testProjectId, testUser)
        }

        assertThat(exception).isSameInstanceAs(expectedError)
    }

    @Test
    fun `should call delete with correct project ID and user ID`() = runTest {
        coEvery { mockRepo.getProjectById(testProjectId.toString()) } returns testProject
        coEvery { mockRepo.deleteProject(testProjectId, testUser) } just Runs

        useCase(testProjectId, testUser)

        coVerify(exactly = 1) {
            mockRepo.deleteProject(testProjectId, testUser)
        }
    }
}