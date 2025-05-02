package logic.usecase.project

import io.mockk.every
import io.mockk.mockk
import org.example.entity.ProjectEntity
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.user.GetProjectUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.util.*
import kotlin.test.Test

class GetProjectUseCaseTest {
    private val mockRepo = mockk<ProjectRepository>()
    private val useCase = GetProjectUseCase(mockRepo)
    private val testProjectId = UUID.randomUUID()

    @Test
    fun `should return project when found`() {
        val expectedProject = mockk<ProjectEntity>()
        every { mockRepo.getProjectById(any()) } returns Result.success(expectedProject)

        val result = useCase(testProjectId)

        assertEquals(expectedProject, result.getOrNull())
    }

    @Test
    fun `should fail when project not found`() {
        every { mockRepo.getProjectById(any()) } returns Result.failure(NoSuchElementException())

        val result = useCase(testProjectId)

        assertTrue(result.isFailure)
    }
}