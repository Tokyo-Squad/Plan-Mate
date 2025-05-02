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
import org.example.logic.usecase.task.UpdateTaskUseCase
import org.junit.jupiter.api.BeforeEach
import org.example.entity.AuditedEntityType
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
 fun `should succeed when repository update succeeds`() {
  // Given
  val userId = UUID.randomUUID()
  val auditedType = AuditedEntityType.TASK
  val task = TaskEntity(
   title = "Title",
   description = "Desc",
   stateId = UUID.randomUUID(),
   projectId = UUID.randomUUID(),
   createdByUserId = userId,
   createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
  )
  every { repository.update(task, auditedType, userId) } returns Result.success(Unit)

  // When
  val result = useCase(task, auditedType, userId)

  // Then
  assertThat(result.isSuccess).isTrue()
  verify { repository.update(task, auditedType, userId) }
 }

 @Test
 fun `should fail when title is blank`() {
  // Given
  val userId = UUID.randomUUID()
  val auditedType = AuditedEntityType.TASK
  val task = TaskEntity(
   title = "",
   description = "Desc",
   stateId = UUID.randomUUID(),
   projectId = UUID.randomUUID(),
   createdByUserId = userId,
   createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
  )

  // When
  val result = useCase(task, auditedType, userId)

  // Then
  assertThat(result.isFailure).isTrue()
  verify(exactly = 0) { repository.update(any(), any(), any()) }
 }

 @Test
 fun `should fail when repository update fails`() {
  // Given
  val userId = UUID.randomUUID()
  val auditedType = AuditedEntityType.TASK
  val task = TaskEntity(
   title = "Title",
   description = "Desc",
   stateId = UUID.randomUUID(),
   projectId = UUID.randomUUID(),
   createdByUserId = userId,
   createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
  )
  val ex = RuntimeException("Update failed")
  every { repository.update(task, auditedType, userId) } returns Result.failure(ex)

  // When
  val result = useCase(task, auditedType, userId)

  // Then
  assertThat(result.isFailure).isTrue()
  verify { repository.update(task, auditedType, userId) }
 }
}