package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import logic.model.Project
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.project.DeleteProjectUseCase
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.Test

class DeleteProjectUseCaseTest {
    private val mockRepo = mockk<ProjectRepository>()
    private val useCase = DeleteProjectUseCase(mockRepo)
    private val testProjectId = UUID.randomUUID()
    private val testUserId = UUID.randomUUID()
    private val testProject = mockk<Project>()

    @Test
    fun `should delete when project exists`() = runTest {
        coEvery { mockRepo.getProjectById(any()) } returns testProject
        coEvery { mockRepo.deleteProject(any(), any()) } just Runs

        useCase(testProjectId, testUserId)

        coVerify {
            mockRepo.getProjectById(testProjectId)
            mockRepo.deleteProject(testProjectId, testUserId)
        }
    }

    @Test
    fun `should throw NoSuchElementException when project not found`() = runTest {
        coEvery { mockRepo.getProjectById(any()) } throws
                NoSuchElementException("Project not found")

        assertThrows<NoSuchElementException> {
            useCase(testProjectId, testUserId)
        }
    }

    @Test
    fun `should propagate delete exceptions`() = runTest {
        val expectedError = RuntimeException("DB error")
        coEvery { mockRepo.getProjectById(any()) } returns testProject
        coEvery { mockRepo.deleteProject(any(), any()) } throws expectedError

        assertThrows<RuntimeException> {
            useCase(testProjectId, testUserId)
        }.also { exception ->
            assertThat(exception).isSameInstanceAs(expectedError)
        }
    }
}