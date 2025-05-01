package logic.usecase.task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.GetTaskByIdUseCase
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GetTaskByIdUseCaseTest {

 private lateinit var taskRepository: TaskRepository
 private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase

 @Before
 fun setUp() {
  taskRepository = mockk()
  getTaskByIdUseCase = GetTaskByIdUseCase(taskRepository)
 }

 @Test
 fun `should return task when task exists`() {
  val taskId = UUID.randomUUID()
  val task = buildDummyTask(taskId)

  coEvery { taskRepository.getTaskById(taskId) } returns Result.success(task)

  val result = getTaskByIdUseCase(taskId)

  assertTrue(result.isSuccess)
  assertEquals(task, result.getOrNull())
  coVerify { taskRepository.getTaskById(taskId) }
 }

 @Test
 fun `should return failure when task does not exist`() {
  val taskId = UUID.randomUUID()

  coEvery { taskRepository.getTaskById(taskId) } returns Result.failure(Exception("Not found"))

  val result = getTaskByIdUseCase(taskId)

  assertFalse(result.isSuccess)
  assertNull(result.getOrNull())
  coVerify { taskRepository.getTaskById(taskId) }
 }

 @Test
 fun `should call repository exactly once`() {
  val taskId = UUID.randomUUID()

  coEvery { taskRepository.getTaskById(taskId) } returns Result.failure(Exception("Error"))

  getTaskByIdUseCase(taskId)

  coVerify(exactly = 1) { taskRepository.getTaskById(taskId) }
 }

 @Test
 fun `should return correct task title`() {
  val taskId = UUID.randomUUID()
  val task = buildDummyTask(taskId, title = "Expected Task")

  coEvery { taskRepository.getTaskById(taskId) } returns Result.success(task)

  val result = getTaskByIdUseCase(taskId)

  assertEquals("Expected Task", result.getOrNull()?.title)
 }

 private fun buildDummyTask(id: UUID, title: String = "Test Task"): TaskEntity {
  return TaskEntity(
   id = id,
   title = title,
   description = "Test Description",
   stateId = UUID.randomUUID(),
   projectId = UUID.randomUUID(),
   createdByUserId = UUID.randomUUID(),
   createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
  )
 }
}
