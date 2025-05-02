package logic.usecase.user.projectuscases

import io.mockk.every
import io.mockk.mockk
import org.example.entity.ProjectEntity
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.user.ListProjectsUseCase
import org.junit.Assert.assertEquals
import kotlin.test.Test

class ListProjectsUseCaseTest {
    private val mockRepo = mockk<ProjectRepository>()
    private val useCase = ListProjectsUseCase(mockRepo)

    @Test
    fun `should return projects list when available`() {
        val expectedProjects = listOf(mockk<ProjectEntity>())
        every { mockRepo.getAllProjects() } returns Result.success(expectedProjects)

        val result = useCase()

        assertEquals(expectedProjects, result.getOrNull())
    }

    @Test
    fun `should return empty list when no projects exist`() {
        every { mockRepo.getAllProjects() } returns Result.success(emptyList())

        val result = useCase()

        assertEquals(emptyList<ProjectEntity>(), result.getOrNull())
    }
}