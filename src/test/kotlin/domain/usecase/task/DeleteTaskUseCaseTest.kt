package domain.usecase.task

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.DeleteTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class DeleteTaskUseCaseTest {
    private lateinit var repository: TaskRepository
    private lateinit var useCase: DeleteTaskUseCase
    private lateinit var id: UUID
    private lateinit var userId: UUID

    @BeforeEach
    fun setUp() {
        id = UUID.randomUUID()
        userId = UUID.randomUUID()
        repository = mockk(relaxed = true)
        useCase = DeleteTaskUseCase(repository)
    }

    @Test
    fun `should succeed when repository delete succeeds`() = runTest {
        // When & Then
        assertDoesNotThrow { useCase(id, userId) }
        coVerify { repository.delete(id, userId) }
    }

    @Test
    fun `should propagate exception when repository throws`() = runTest {
        // Given
        coEvery { repository.delete(id, userId) } throws RuntimeException("Delete failed")

        // When & Then
        assertThrows<RuntimeException> { useCase(id, userId) }
        coVerify { repository.delete(id, userId) }
    }
}