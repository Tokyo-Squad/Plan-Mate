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
import org.example.logic.usecase.task.GetTasksByProjectIdUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
 fun `should return list when repository returns data`() {
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
  every { repository.getTasksByProjectId(projectId) } returns Result.success(list)

  // When
  val result = useCase(projectId)

  // Then
  assertThat(result.getOrThrow()).isEqualTo(list)
  verify { repository.getTasksByProjectId(projectId) }
 }

 @Test
 fun `should fail when repository getByProjectId fails`() {
  // Given
  val ex = RuntimeException("Fetch failed")
  every { repository.getTasksByProjectId(projectId) } returns Result.failure(ex)

  // When
  val result = useCase(projectId)

  // Then
  assertThat(result.isFailure).isTrue()
  verify { repository.getTasksByProjectId(projectId) }
 }
}