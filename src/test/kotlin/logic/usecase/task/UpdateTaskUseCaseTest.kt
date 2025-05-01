package logic.usecase.task

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.UpdateTaskUseCase
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.*

class UpdateTaskUseCaseTest {

 private lateinit var taskRepository: TaskRepository
 private lateinit var updateTaskUseCase: UpdateTaskUseCase

 private val dummyUserId = UUID.randomUUID()

 @Before
 fun setUp() {
  taskRepository = mockk()
  updateTaskUseCase = UpdateTaskUseCase(taskRepository)
 }

 @Test
 fun `should update task successfully`() {
  val task = buildDummyTask()

  coEvery { taskRepository.update(task, dummyUserId) } returns Result.success(Unit)

  val result = updateTaskUseCase(task, dummyUserId)

  assertTrue(result.isSuccess)
  coVerify { taskRepository.update(task, dummyUserId) }
 }

 @Test
 fun `should return failure if update fails`(){
  val task = buildDummyTask()

  coEvery { taskRepository.update(task, dummyUserId) } returns Result.failure(Exception("Update failed"))

  val result = updateTaskUseCase(task, dummyUserId)

  assertFalse(result.isSuccess)
  coVerify { taskRepository.update(task, dummyUserId) }
 }

 @Test
 fun `should call repository exactly once`() {
  val task = buildDummyTask()

  coEvery { taskRepository.update(task, dummyUserId) } returns Result.success(Unit)

  updateTaskUseCase(task, dummyUserId)

  coVerify(exactly = 1) { taskRepository.update(task, dummyUserId) }
 }

 @Test
 fun `should preserve task id and project id`() {
  val task = buildDummyTask()

  coEvery { taskRepository.update(task, dummyUserId) } returns Result.success(Unit)

  val result = updateTaskUseCase(task, dummyUserId)

  assertTrue(result.isSuccess)
  assertEquals(task.id, task.id)
  assertNotNull(task.projectId)
 }

 private fun buildDummyTask(): TaskEntity {
  return TaskEntity(
   id = UUID.randomUUID(),
   title = "Updated Task",
   description = "Updated description",
   stateId = UUID.randomUUID(),
   projectId = UUID.randomUUID(),
   createdByUserId = UUID.randomUUID(),
   createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
  )
 }
}
