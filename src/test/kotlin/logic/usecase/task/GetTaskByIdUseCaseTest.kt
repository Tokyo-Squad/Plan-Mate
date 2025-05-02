package logic.usecase.task

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.GetTaskByIdUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
 fun `should return task when repository finds by id`() {
  // Given
  val task = TaskEntity(
   title = "Title",
   description = "Desc",
   stateId = UUID.randomUUID(),
   projectId = UUID.randomUUID(),
   createdByUserId = UUID.randomUUID(),
   createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
  )
  every { repository.getTaskById(id) } returns Result.success(task)

  // When
  val result = useCase(id)

  // Then
  assertThat(result.getOrThrow()).isEqualTo(task)
  verify { repository.getTaskById(id) }
 }

 @Test
 fun `should fail when repository getById fails`() {
  // Given
  val ex = RuntimeException("Fetch failed")
  every { repository.getTaskById(id) } returns Result.failure(ex)

  // When
  val result = useCase(id)

  // Then
  assertThat(result.isFailure)
  verify { repository.getTaskById(id) }
 }
}