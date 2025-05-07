package logic.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.GetTasksByProjectIdUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class GetTasksByProjectIdUseCaseTest {
    private lateinit var repository: TaskRepository
    private lateinit var useCase: GetTasksByProjectIdUseCase
    private lateinit var projectId: UUID

    @BeforeEach
    fun setUp() {
        projectId = UUID.randomUUID()
        repository = mockk(relaxed = true)
        useCase = GetTasksByProjectIdUseCase(repository)
    }

    @Test
    fun `should return list when repository returns data`() = runBlocking {
        // Given
        val list = listOf(
            TaskEntity(
                title = "Title",
                description = "Desc",
                stateId = UUID.randomUUID(),
                projectId = UUID.randomUUID(),
                createdByUserId = UUID.randomUUID(),
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            )
        )
        coEvery { repository.getTasksByProjectId(projectId) } returns list

        // When
        val result = useCase(projectId)

        // Then
        assertThat(result).isEqualTo(list)
        coVerify { repository.getTasksByProjectId(projectId) }
    }

    @Test
    fun `should propagate exception when repository throws`() = runBlocking {
        // Given
        coEvery { repository.getTasksByProjectId(projectId) } throws RuntimeException("Fetch failed")

        // When & Then
        assertThrows<RuntimeException> {
            runBlocking { useCase(projectId) }
        }
        coVerify { repository.getTasksByProjectId(projectId) }
    }
}