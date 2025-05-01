package logic.usecase.task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.CreateTaskUseCase
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class CreateTaskUseCaseTest {

 private lateinit var taskRepository: TaskRepository
 private lateinit var createTaskUseCase: CreateTaskUseCase

 @Before
 fun setUp() {
  taskRepository = mockk()
  createTaskUseCase = CreateTaskUseCase(taskRepository)
 }

 @Test
 fun `should create task successfully`(){
  val task = buildValidTask()
  val currentUserId = task.createdByUserId

  coEvery { taskRepository.create(task, currentUserId) } returns Result.success(Unit)

  val result = createTaskUseCase(task, currentUserId)

  assertTrue(result.isSuccess)
  coVerify { taskRepository.create(task, currentUserId) }
 }

 @Test
 fun `should fail to create task when repository returns error`()  {
  val task = buildValidTask()
  val currentUserId = task.createdByUserId

  coEvery { taskRepository.create(task, currentUserId) } returns Result.failure(Exception("DB error"))

  val result = createTaskUseCase(task, currentUserId)

  assertFalse(result.isSuccess)
  coVerify { taskRepository.create(task, currentUserId) }
 }

 @Test
 fun `should create task with different stateId`()  {
  val task = buildValidTask().copy(stateId = UUID.randomUUID())
  val currentUserId = task.createdByUserId

  coEvery { taskRepository.create(task, currentUserId) } returns Result.success(Unit)

  val result = createTaskUseCase(task, currentUserId)

  assertTrue(result.isSuccess)
  coVerify { taskRepository.create(task, currentUserId) }
 }

 @Test
 fun `should fail when current user ID is not the creator`()  {
  val task = buildValidTask()
  val wrongUserId = UUID.randomUUID()


  coEvery { taskRepository.create(task, wrongUserId) } returns Result.failure(Exception("Unauthorized"))

  val result = createTaskUseCase(task, wrongUserId)

  assertFalse(result.isSuccess)
  coVerify { taskRepository.create(task, wrongUserId) }
 }

 private fun buildValidTask(): TaskEntity {
  return TaskEntity(
   title = "Sample Task",
   description = "This is a test",
   stateId = UUID.randomUUID(),
   projectId = UUID.randomUUID(),
   createdByUserId = UUID.randomUUID(),
   createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
  )
 }
}
