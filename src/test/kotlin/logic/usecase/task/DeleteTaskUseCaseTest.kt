package logic.usecase.task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.DeleteTaskUseCase
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class DeleteTaskUseCaseTest {

 private lateinit var taskRepository: TaskRepository
 private lateinit var deleteTaskUseCase: DeleteTaskUseCase

 @Before
 fun setUp() {
  taskRepository = mockk()
  deleteTaskUseCase = DeleteTaskUseCase(taskRepository)
 }

 @Test
 fun `should delete task successfully`()  {
  val taskId = UUID.randomUUID()
  val currentUserId = UUID.randomUUID()

  coEvery { taskRepository.delete(taskId, currentUserId) } returns Result.success(Unit)

  val result = deleteTaskUseCase(taskId, currentUserId)

  assertTrue(result.isSuccess)
  coVerify { taskRepository.delete(taskId, currentUserId) }
 }

 @Test
 fun `should fail to delete non-existing task`()  {
  val taskId = UUID.randomUUID()
  val currentUserId = UUID.randomUUID()

  coEvery { taskRepository.delete(taskId, currentUserId) } returns Result.failure(Exception("Task not found"))

  val result = deleteTaskUseCase(taskId, currentUserId)

  assertFalse(result.isSuccess)
  coVerify { taskRepository.delete(taskId, currentUserId) }
 }

 @Test
 fun `should fail to delete task without permission`() {
  val taskId = UUID.randomUUID()
  val currentUserId = UUID.randomUUID()

  coEvery { taskRepository.delete(taskId, currentUserId) } returns Result.failure(Exception("Unauthorized"))

  val result = deleteTaskUseCase(taskId, currentUserId)

  assertFalse(result.isSuccess)
  coVerify { taskRepository.delete(taskId, currentUserId) }
 }

 @Test
 fun `should call repository delete method exactly once`() {
  val taskId = UUID.randomUUID()
  val currentUserId = UUID.randomUUID()

  coEvery { taskRepository.delete(taskId, currentUserId) } returns Result.success(Unit)

  deleteTaskUseCase(taskId, currentUserId)

  coVerify(exactly = 1) { taskRepository.delete(taskId, currentUserId) }
 }
}

