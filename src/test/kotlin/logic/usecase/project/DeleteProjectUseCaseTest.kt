package logic.usecase.project

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.user.DeleteProjectUseCase
import org.junit.Assert.assertTrue
import java.util.*
import kotlin.test.Test

class DeleteProjectUseCaseTest {
    private val mockRepo = mockk<ProjectRepository>()
    private val useCase = DeleteProjectUseCase(mockRepo)
    private val testProjectId = UUID.randomUUID()
    private val testUser = UUID.randomUUID()

    @Test
    fun `should return success when deletion succeeds`() {
        every { mockRepo.getProjectById(any()) } returns Result.success(mockk())
        every { mockRepo.deleteProject(any(), any()) } returns Result.success(Unit)

        val result = useCase(testProjectId, testUser)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `should fail when project not found`() {
        every { mockRepo.getProjectById(any()) } returns Result.failure(NoSuchElementException())

        val result = useCase(testProjectId, testUser)

        assertTrue(result.isFailure)
    }

    @Test
    fun `should call delete with correct project ID`() {
        every { mockRepo.getProjectById(any()) } returns Result.success(mockk())
        every { mockRepo.deleteProject(any(), any()) } returns Result.success(Unit)

        useCase(testProjectId, testUser)

        verify { mockRepo.deleteProject(testProjectId, any()) }
    }
}