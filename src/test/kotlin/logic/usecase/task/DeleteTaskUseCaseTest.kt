package logic.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.DeleteTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
 fun `should succeed when repository delete succeeds`() {
  // Given
  every { repository.delete(id, userId) } returns Result.success(Unit)

  // When
  val result = useCase(id, userId)

  // Then
  assertThat(result.isSuccess)
  verify { repository.delete(id, userId) }
 }

 @Test
 fun `should fail when repository delete fails`() {
  // Given
  val ex = RuntimeException("Delete failed")
  every { repository.delete(id, userId) } returns Result.failure(ex)

  // When
  val result = useCase(id, userId)

  // Then
  assertThat(result.isFailure)
  verify { repository.delete(id, userId) }
 }
}