package logic.usecase.task

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.entity.TaskEntity
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
        val task = TaskEntity(
            title = "Title",
            description = "Desc",
            stateId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            createdByUserId = userId,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )

        // When & Then
        assertDoesNotThrow { useCase(task, userId) }
        coVerify { repository.update(task, userId) }
    }

    @Test
    fun `should throw IllegalArgumentException when title is blank`() = runTest {
        // Given
        val userId = UUID.randomUUID()
        val taskWithBlankTitle = TaskEntity(
            title = "",
            description = "Desc",
            stateId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            createdByUserId = userId,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )

        // When & Then
        assertThrows<IllegalArgumentException> { useCase(taskWithBlankTitle, userId) }
        coVerify(exactly = 0) { repository.update(any(), any()) }
    }

    @Test
    fun `should propagate exception when repository throws`() = runTest {
        // Given
        val userId = UUID.randomUUID()
        val task = TaskEntity(
            title = "Title",
            description = "Desc",
            stateId = UUID.randomUUID(),
            projectId = UUID.randomUUID(),
            createdByUserId = userId,
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )

        coEvery { repository.update(task, userId) } throws RuntimeException("Update failed")

        // When & Then
        assertThrows<RuntimeException> { useCase(task, userId) }
        coVerify { repository.update(task, userId) }
    }
}