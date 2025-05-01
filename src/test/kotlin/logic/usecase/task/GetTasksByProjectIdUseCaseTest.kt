package logic.usecase.task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.GetTasksByProjectIdUseCase
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse


class GetTasksByProjectIdUseCaseTest {

 private lateinit var taskRepository: TaskRepository
 private lateinit var getTasksByProjectIdUseCase: GetTasksByProjectIdUseCase

 @Before
 fun setUp() {
  taskRepository = mockk()
  getTasksByProjectIdUseCase = GetTasksByProjectIdUseCase(taskRepository)
 }

 @Test
 fun `should return list of tasks when tasks exist for project`() {
  val projectId = UUID.randomUUID()
  val taskList = listOf(buildDummyTask(), buildDummyTask())

  coEvery { taskRepository.getTasksByProjectId(projectId) } returns Result.success(taskList)

  val result = getTasksByProjectIdUseCase(projectId)

  assertTrue(result.isSuccess)
  assertEquals(2, result.getOrNull()?.size)
  coVerify { taskRepository.getTasksByProjectId(projectId) }
 }

 @Test
 fun `should return empty list when no tasks exist for project`() {
  val projectId = UUID.randomUUID()

  coEvery { taskRepository.getTasksByProjectId(projectId) } returns Result.success(emptyList())

  val result = getTasksByProjectIdUseCase(projectId)

  assertTrue(result.isSuccess)
  assertTrue(result.getOrNull()?.isEmpty() == true)
  coVerify { taskRepository.getTasksByProjectId(projectId) }
 }

 @Test
 fun `should return failure when repository throws error`() {
  val projectId = UUID.randomUUID()

  coEvery { taskRepository.getTasksByProjectId(projectId) } returns Result.failure(Exception("DB error"))

  val result = getTasksByProjectIdUseCase(projectId)

  assertFalse(result.isSuccess)
  coVerify { taskRepository.getTasksByProjectId(projectId) }
 }

 @Test
 fun `should call repository method exactly once`() {
  val projectId = UUID.randomUUID()

  coEvery { taskRepository.getTasksByProjectId(projectId) } returns Result.success(emptyList())

  getTasksByProjectIdUseCase(projectId)

  coVerify(exactly = 1) { taskRepository.getTasksByProjectId(projectId) }
 }

 private fun buildDummyTask(): TaskEntity {
  return TaskEntity(
   id = UUID.randomUUID(),
   title = "Sample Task",
   description = "Sample Description",
   stateId = UUID.randomUUID(),
   projectId = UUID.randomUUID(),
   createdByUserId = UUID.randomUUID(),
   createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
  )
 }
}
