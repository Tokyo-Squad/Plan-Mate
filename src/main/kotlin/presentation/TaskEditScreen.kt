package org.example.presentation

import SwimlaneRenderer
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.entity.TaskEntity
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.example.logic.usecase.project.GetProjectUseCase
import org.example.logic.usecase.state.GetStatesByProjectId
import org.example.logic.usecase.task.*
import org.example.presentation.io.ConsoleIO
import java.util.UUID

class TaskEditScreen(
    private val console: ConsoleIO,
    private val getTaskUseCase: GetTaskByIdUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getTasksByProjectUseCase: GetTasksByProjectIdUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val getStatesByProjectId: GetStatesByProjectId,
    private val getProjectUseCase: GetProjectUseCase,
    private val swimlaneRenderer: SwimlaneRenderer
) {
    private val currentUser by lazy { runBlocking { getCurrentUserUseCase() } }

    fun displayTaskEditor(taskId: String) = runBlocking {
        loop@ while (true) {
            try {
                console.write("\n=== Edit Task ===")
                when (showMenu()) {
                    1 -> editTaskTitle(taskId)
                    2 -> editTaskDescription(taskId)
                    3 -> handleTaskDeletion(taskId)
                    4 -> break@loop
                    else -> console.writeError("Invalid option. Please try again.")
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    fun manageProjectTasks(projectId: UUID) = runBlocking {
        try {
            val project = getProjectUseCase(projectId) ?: run {
                console.writeError("Project not found")
                return@runBlocking
            }

            loop@ while (true) {
                console.write("\n=== Tasks for Project: ${project.name} ===")
                when (showProjectTasksMenu()) {
                    1 -> viewTasks(projectId)
                    2 -> createTask(projectId)
                    3 -> editSpecificTask()
                    4 -> updateTaskStatus(projectId)
                    5 -> deleteTaskFromProject(projectId)
                    6 -> break@loop
                    else -> console.writeError("Invalid option. Please try again.")
                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private suspend fun editTaskTitle(taskId: String) = modifyTask(taskId) { task ->
        task.copy(title = readNonEmptyInput("Enter new title: "))
    }

    private suspend fun editTaskDescription(taskId: String) = modifyTask(taskId) { task ->
        task.copy(description = readNonEmptyInput("Enter new description: "))
    }

    private suspend fun handleTaskDeletion(taskId: String) {
        if (!confirmAction("Are you sure you want to delete this task?")) {
            console.write("Deletion cancelled")
            return
        }

        deleteTaskUseCase(taskId.toUUID(), currentUserId())
        console.write("Task deleted successfully!")
    }

    private suspend fun createTask(projectId: UUID) {
        console.write("\n=== Create New Task ===")
        val title = readNonEmptyInput("Enter task title: ")
        val description = readNonEmptyInput("Enter task description: ")

        val newTask = TaskEntity(
            title = title,
            description = description,
            stateId = getInitialState(projectId),
            projectId = projectId,
            createdByUserId = currentUserId(),
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        createTaskUseCase(newTask, currentUserId())
        console.write("Task created successfully!")
    }

    private suspend fun updateTaskStatus(projectId: UUID) {
        viewTasks(projectId)
        val taskId = readUUIDInput("\nEnter task ID to update: ")
        val newStateId = selectState(projectId)

        modifyTask(taskId.toString()) { it.copy(stateId = newStateId) }
        console.write("Task status updated successfully!")
    }

    private suspend fun deleteTaskFromProject(projectId: UUID) {
        viewTasks(projectId)
        val taskId = readUUIDInput("\nEnter task ID to delete: ")

        if (confirmAction("Confirm deletion?")) {
            deleteTaskUseCase(taskId, currentUserId())
            console.write("Task deleted successfully!")
        } else {
            console.write("Deletion cancelled")
        }
    }

    private suspend fun modifyTask(
        taskId: String,
        update: suspend (TaskEntity) -> TaskEntity
    ) {
        val task = getTaskUseCase(taskId.toUUID())
        val updatedTask = update(task)
        updateTaskUseCase(updatedTask, currentUserId())
    }

    private fun getInitialState(projectId: UUID): UUID {
        return getStatesByProjectId(projectId).firstOrNull()?.id
            ?: throw IllegalStateException("No states available for this project")
    }

    private fun selectState(projectId: UUID): UUID {
        val states = getStatesByProjectId(projectId)
        states.forEachIndexed { i, st -> console.write("${i + 1}. ${st.name}") }
        val index = readValidIndex("Select new state: ", states.size)
        return states[index].id
    }

    private fun showProjectTasksMenu(): Int {
        return readMenuSelection(
            """
            1. View Tasks
            2. Create Task
            3. Edit Task
            4. Update Status
            5. Delete Task
            6. Back
            
            Select an option: 
            """.trimIndent()
        )
    }

    private fun showMenu(): Int {
        return readMenuSelection(
            """
            1. Edit Title
            2. Edit Description
            3. Delete Task
            4. Back
            
            Select an option: 
            """.trimIndent()
        )
    }

    private fun readNonEmptyInput(prompt: String): String {
        return readInput(prompt) { it.isNotBlank() }
            ?: throw IllegalArgumentException("Input cannot be empty")
    }

    private fun readUUIDInput(prompt: String): UUID {
        return readInput(prompt) { it.isValidUUID() }?.toUUID()
            ?: throw IllegalArgumentException("Invalid UUID format")
    }

    private fun readValidIndex(prompt: String, max: Int): Int {
        val input = readInput(prompt) { it.isValidIndex(max) }
        return input?.toInt()?.minus(1)
            ?: throw IllegalArgumentException("Invalid selection")
    }

    private fun readMenuSelection(menuText: String): Int {
        console.write(menuText)
        return readInput("") { it.isValidMenuOption() }?.toInt()
            ?: throw IllegalArgumentException("Invalid menu selection")
    }

    private inline fun readInput(
        prompt: String,
        validation: (String) -> Boolean
    ): String? {
        console.write(prompt)
        return console.read().trim().takeIf(validation)
    }

    private fun confirmAction(prompt: String): Boolean {
        return readInput("$prompt (yes/no): ") { it.equals("yes", true) } != null
    }

    private fun String.isValidUUID() = runCatching { UUID.fromString(this) }.isSuccess
    private fun String.isValidIndex(max: Int) = toIntOrNull()?.let { it in 1..max } ?: false
    private fun String.isValidMenuOption() = toIntOrNull()?.let { it > 0 } ?: false

    private fun currentUserId() = currentUser?.id
        ?: throw IllegalStateException("User not authenticated")

    private suspend fun viewTasks(projectId: UUID) {
        val tasks = getTasksByProjectUseCase(projectId)
        val states = getStatesByProjectId(projectId).associate { it.id to it.name }
        swimlaneRenderer.render(tasks, states)
    }

    private fun editSpecificTask() {
        displayTaskEditor(readUUIDInput("Enter task ID: ").toString())
    }

    private fun handleException(e: Exception) {
        console.writeError("Error: ${e.message ?: "Unknown error"}")
    }

    private fun String.toUUID() = UUID.fromString(this)
}