package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import logic.model.Project
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.project.ListProjectsUseCase
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class ListProjectsUseCaseTest {
    private val mockRepo = mockk<ProjectRepository>()
    private val useCase = ListProjectsUseCase(mockRepo)

    @Test
    fun `should return projects list when available`() = runTest {
        val expectedProjects = listOf(mockk<Project>())
        coEvery { mockRepo.getAllProjects() } returns expectedProjects

        val result = useCase()

        assertThat(result).isEqualTo(expectedProjects)
    }

    @Test
    fun `should return empty list when no projects exist`() = runTest {
        coEvery { mockRepo.getAllProjects() } returns emptyList()

        val result = useCase()

        assertThat(result).isEqualTo(emptyList<Project>())
    }

    @Test
    fun `should propagate repository exceptions`() = runTest {
        val expectedError = RuntimeException("Database error")
        coEvery { mockRepo.getAllProjects() } throws expectedError

        val exception = assertThrows<RuntimeException> {
            useCase()
        }

        assertThat(exception).isSameInstanceAs(expectedError)
    }
}