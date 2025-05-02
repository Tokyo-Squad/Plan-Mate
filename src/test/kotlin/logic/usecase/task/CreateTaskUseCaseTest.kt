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
import org.example.logic.usecase.task.CreateTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class CreateTaskUseCaseTest {
 private lateinit var repository: TaskRepository
 private lateinit var useCase: CreateTaskUseCase

 @BeforeEach
 fun setUp() {
  repository = mockk(relaxed = true)
  useCase = CreateTaskUseCase(repository)
 }

 @Test
 fun `should succeed when repository create succeeds`() {
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
  every { repository.create(task, userId) } returns Result.success(Unit)

  // When
  val result = useCase(task, userId)

  // Then
  assertThat(result.isSuccess).isTrue()
  verify { repository.create(task, userId) }
 }

 @Test
 fun `should fail when repository create fails`() {
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
  val ex = RuntimeException("Create failed")
  every { repository.create(task, userId) } returns Result.failure(ex)

  // When
  val result = useCase(task, userId)

  // Then
  assertThat(result.isFailure).isTrue()
  verify { repository.create(task, userId) }
 }

 @Test
 fun `should fail when title is blank`() {
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

  // When
  val result = useCase(taskWithBlankTitle, userId)

  // Then
  assertThat(result.isFailure).isTrue()
  verify(exactly = 0) { repository.create(any(), any()) }
 }

}