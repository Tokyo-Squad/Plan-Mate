package domain.usecase.task

import fakeData.createTaskEntityTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.UpdateTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.Test

class UpdateTaskUseCaseTest {
    private lateinit var repository: TaskRepository
    private lateinit var useCase: UpdateTaskUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = UpdateTaskUseCase(repository)
    }

    @Test
    fun `should succeed when repository update succeeds`() = runTest {
        // Given
        val userId = UUID.randomUUID()
        val task = createTaskEntityTest()
        // When & Then
        assertDoesNotThrow { useCase(task, userId) }
        coVerify { repository.update(task, userId) }
    }

    @Test
    fun `should throw IllegalArgumentException when title is blank`() = runTest {
        // Given
        val userId = UUID.randomUUID()
        val taskWithBlankTitle = createTaskEntityTest(title = "")
        // When & Then
        assertThrows<IllegalArgumentException> { useCase(taskWithBlankTitle, userId) }
        coVerify(exactly = 0) { repository.update(any(), any()) }
    }

    @Test
    fun `should propagate exception when repository throws`() = runTest {
        // Given
        val userId = UUID.randomUUID()
        val task = createTaskEntityTest()
        coEvery { repository.update(task, userId) } throws RuntimeException("Update failed")

        // When & Then
        assertThrows<RuntimeException> { useCase(task, userId) }
        coVerify { repository.update(task, userId) }
    }
}