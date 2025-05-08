package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.entity.ProjectEntity
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.project.GetProjectUseCase
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.Test

class GetProjectUseCaseTest {
    private val mockRepo = mockk<ProjectRepository>()
    private val useCase = GetProjectUseCase(mockRepo)
    private val testProjectId = UUID.randomUUID()

    @Test
    fun `should return project when found`() = runTest {
        val expectedProject = mockk<ProjectEntity>()
        coEvery { mockRepo.getProjectById(testProjectId.toString()) } returns expectedProject

        val result = useCase(testProjectId)

        assertThat(result).isEqualTo(expectedProject)
    }

    @Test
    fun `should throw NoSuchElementException when project not found`() = runTest {
        coEvery { mockRepo.getProjectById(any()) } throws NoSuchElementException("Project not found")

        val exception = assertThrows<NoSuchElementException> {
            useCase(testProjectId)
        }

        assertThat(exception).hasMessageThat().contains("Project not found")
    }

    @Test
    fun `should propagate repository exceptions`() = runTest {
        val expectedError = RuntimeException("Database error")
        coEvery { mockRepo.getProjectById(any()) } throws expectedError

        val exception = assertThrows<RuntimeException> {
            useCase(testProjectId)
        }

        assertThat(exception).isSameInstanceAs(expectedError)
    }
}