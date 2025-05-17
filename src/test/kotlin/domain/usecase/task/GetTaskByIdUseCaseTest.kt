package domain.usecase.task

import com.google.common.truth.Truth.assertThat
import fakeData.createTaskEntityTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.GetTaskByIdUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID


class GetTaskByIdUseCaseTest {
    private lateinit var repository: TaskRepository
    private lateinit var useCase: GetTaskByIdUseCase
    private lateinit var id: UUID

    @BeforeEach
    fun setUp() {
        id = UUID.randomUUID()
        repository = mockk(relaxed = true)
        useCase = GetTaskByIdUseCase(repository)
    }

    @Test
    fun `should return task when repository finds by id`() = runTest {
        // Given
        val task = createTaskEntityTest()
        coEvery { repository.getTaskById(id) } returns task

        // When
        val result = useCase(id)

        // Then
        assertThat(result).isEqualTo(task)
        coVerify { repository.getTaskById(id) }
    }

    @Test
    fun `should propagate exception when repository throws`() = runTest {
        // Given
        coEvery { repository.getTaskById(id) } throws RuntimeException("Fetch failed")

        // When & Then
        assertThrows<RuntimeException> { useCase(id) }
        coVerify { repository.getTaskById(id) }
    }
}
