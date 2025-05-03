package org.example.presentation

import SwimlaneRenderer
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.entity.TaskEntity
import org.example.logic.usecase.GetStatesByProjectId
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.example.logic.usecase.project.GetProjectUseCase
import org.example.logic.usecase.task.*
import org.example.presentation.io.ConsoleIO
import java.util.*

class TaskEditScreen(
    private val console: ConsoleIO,
    private val getTaskUseCase: GetTaskByIdUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    // Add these new dependencies
    private val getTasksByProjectUseCase: GetTasksByProjectIdUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val getStatesByProjectId: GetStatesByProjectId,
    private val getProjectUseCase: GetProjectUseCase,
    private val swimlaneRenderer: SwimlaneRenderer
) {
    // Existing show method for editing a specific task
    fun show(taskId: String) {
        while (true) {
            try {
                console.write("\n=== Edit Task ===")
                when (showMenu()) {
                    1 -> editTaskTitle(taskId)
                    2 -> editTaskDescription(taskId)
                    3 -> handleTaskDeletion(taskId)
                    4 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            } catch (e: Exception) {
                console.writeError("Error: ${e.message}")
            }
        }
    }

    // New method to show tasks for a project and manage them
    fun showTasksForProject(projectId: UUID) {
        try {
            val project = getProjectUseCase(projectId).getOrElse { e ->
                console.writeError("Failed to load project: ${e.message}")
                return
            }

            while (true) {
                console.write("\n=== Tasks for Project: ${project.name} ===")
                when (showProjectTasksMenu()) {
                    1 -> viewTasks(projectId)
                    2 -> createTask(projectId)
                    3 -> editSpecificTask()
                    4 -> updateTaskStatus(projectId)
                    5 -> deleteTaskFromProject(projectId)
                    6 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            }
        } catch (e: Exception) {
            console.writeError("Failed to manage tasks: ${e.message}")
        }
    }

    private fun showProjectTasksMenu(): Int {
        console.write("1. View Tasks")
        console.write("2. Create Task")
        console.write("3. Edit Task")
        console.write("4. Update Status")
        console.write("5. Delete Task")
        console.write("6. Back")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }

    // Existing methods
    private fun showMenu(): Int {
        console.write("1. Edit Title")
        console.write("2. Edit Description")
        console.write("3. Delete Task")
        console.write("4. Back")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }

    private fun editTaskTitle(taskId: String) {
        console.write("Enter new title: ")
        val newTitle = console.read().trim()

        if (newTitle.isBlank()) {
            console.writeError("Title cannot be empty")
            return
        }

        try {
            val taskUUID = UUID.fromString(taskId)

            // Get current task
            val currentTask = getTaskUseCase(taskUUID).getOrElse { e ->
                console.writeError("Failed to get task: ${e.message}")
                return
            }

            // Get current user
            val currentUser = getCurrentUserUseCase().getOrElse { e ->
                console.writeError("Failed to get current user: ${e.message}")
                return
            }

            // Create updated task
            val updatedTask = currentTask.copy(
                title = newTitle
            )

            // Update task
            updateTaskUseCase(
                task = updatedTask,
                currentUserId = currentUser!!.id
            ).onSuccess {
                console.write("Task title updated successfully!")
            }.onFailure { e ->
                console.writeError("Failed to update task title: ${e.message}")
            }

        } catch (e: Exception) {
            console.writeError("Failed to update task title: ${e.message}")
        }
    }

    private fun editTaskDescription(taskId: String) {
        console.write("Enter new description: ")
        val newDescription = console.read().trim()

        if (newDescription.isBlank()) {
            console.writeError("Description cannot be empty")
            return
        }

        try {
            val taskUUID = UUID.fromString(taskId)

            // Get current task
            val currentTask = getTaskUseCase(taskUUID).getOrElse { e ->
                console.writeError("Failed to get task: ${e.message}")
                return
            }

            // Get current user
            val currentUser = getCurrentUserUseCase().getOrElse { e ->
                console.writeError("Failed to get current user: ${e.message}")
                return
            }

            // Create updated task
            val updatedTask = currentTask.copy(
                description = newDescription
            )

            // Update task
            updateTaskUseCase(
                task = updatedTask,
                currentUserId = currentUser!!.id
            ).onSuccess {
                console.write("Task description updated successfully!")
            }.onFailure { e ->
                console.writeError("Failed to update task description: ${e.message}")
            }

        } catch (e: Exception) {
            console.writeError("Failed to update task description: ${e.message}")
        }
    }

    private fun handleTaskDeletion(taskId: String) {
        console.write("Are you sure you want to delete this task? (yes/no): ")
        val confirmation = console.read().trim().lowercase()

        if (confirmation != "yes") {
            console.write("Task deletion cancelled")
            return
        }

        try {
            val taskUUID = UUID.fromString(taskId)

            // Get current user
            val currentUser = getCurrentUserUseCase().getOrElse { e ->
                console.writeError("Failed to get current user: ${e.message}")
                return
            }

            // Delete task
            deleteTaskUseCase(
                id = taskUUID,
                currentUserId = currentUser!!.id
            ).onSuccess {
                console.write("Task deleted successfully!")
            }.onFailure { e ->
                console.writeError("Failed to delete task: ${e.message}")
            }

        } catch (e: Exception) {
            console.writeError("Failed to delete task: ${e.message}")
        }
    }

    // New methods moved from ProjectScreen
    private fun viewTasks(projectId: UUID) {
        try {
            val tasks = getTasksByProjectUseCase(projectId).getOrElse { e ->
                console.writeError("Failed to load project: ${e.message}")
                return
            }
            val project = getProjectUseCase(projectId).getOrElse { e ->
                console.writeError("Failed to load project: ${e.message}")
                return
            }
            val states = getStatesByProjectId(project.id)
                .getOrElse { exception ->
                    console.writeError("Failed to load states: ${exception.message}")
                    return
                }
            val stateMap = states.associateBy({ it.id }, { it.name })
            swimlaneRenderer.render(tasks, stateMap)
        } catch (e: Exception) {
            console.writeError("Failed to load tasks: ${e.message}")
        }
    }

    private fun createTask(projectId: UUID) {
        console.write("\n=== Create New Task ===")

        console.write("Enter task title: ")
        val title = console.read().trim()

        if (title.isBlank()) {
            console.writeError("Task title cannot be empty")
            return
        }

        console.write("Enter task description: ")
        val description = console.read().trim()

        if (description.isBlank()) {
            console.writeError("Task description cannot be empty")
            return
        }

        try {
            val currentUser = getCurrentUserUseCase().getOrElse { e ->
                console.writeError("Failed to get Current User: $e")
                return
            }

            // Get initial state for the project
            val states = getStatesByProjectId(projectId).getOrElse { e ->
                console.writeError("Failed to get states: $e")
                return
            }

            // Assuming the first state is the initial state
            val initialState = states.firstOrNull()?.id ?: run {
                console.writeError("No states found for this project")
                return
            }

            val newTask = TaskEntity(
                title = title,
                description = description,
                stateId = initialState,
                projectId = projectId,
                createdByUserId = currentUser!!.id,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )

            createTaskUseCase(newTask, currentUser.id).onSuccess {
                console.write("Task created successfully!")
            }.onFailure { e ->
                console.writeError("Failed to create task: ${e.message}")
            }

        } catch (e: Exception) {
            console.writeError("Failed to create task: ${e.message}")
        }
    }

    private fun editSpecificTask() {
        console.write("Enter task ID: ")
        val taskId = console.read().trim()

        if (taskId.isBlank()) {
            console.writeError("Task ID cannot be empty")
            return
        }

        try {
            show(taskId)
        } catch (e: Exception) {
            console.writeError("Failed to edit task: ${e.message}")
        }
    }

    private fun updateTaskStatus(projectId: UUID) {
        try {
            viewTasks(projectId)

            console.write("\nEnter task ID to update: ")
            val taskId = console.read().trim()

            if (taskId.isBlank()) {
                console.writeError("Task ID cannot be empty")
                return
            }

            // Get current task
            val task = getTaskUseCase(UUID.fromString(taskId)).getOrElse { e ->
                console.writeError("Failed to get task: ${e.message}")
                return
            }

            // Get states and handle the Result
            val states = getStatesByProjectId(projectId).getOrElse { e ->
                console.writeError("Failed to get states: ${e.message}")
                return
            }

            if (states.isEmpty()) {
                console.writeError("No states available for this project")
                return
            }

            console.write("\nAvailable states:")
            states.forEachIndexed { index, state ->
                console.write("${index + 1}. ${state.name}")
            }

            console.write("\nSelect new state (enter number): ")
            val stateIndex = console.read().toIntOrNull()?.minus(1)

            if (stateIndex == null || stateIndex !in states.indices) {
                console.writeError("Invalid state selection")
                return
            }

            // Get current user
            val currentUser = getCurrentUserUseCase().getOrElse { e ->
                console.writeError("Failed to get current user: ${e.message}")
                return
            }

            // Create updated task with new state
            val updatedTask = task.copy(
                stateId = states[stateIndex].id
            )

            // Update the task
            updateTaskUseCase(
                task = updatedTask,
                currentUserId = currentUser!!.id
            ).onSuccess {
                console.write("Task status updated successfully!")
            }.onFailure { e ->
                console.writeError("Failed to update task state: ${e.message}")
            }

        } catch (e: Exception) {
            console.writeError("Failed to update task status: ${e.message}")
        }
    }

    private fun deleteTaskFromProject(projectId: UUID) {
        try {
            viewTasks(projectId)

            console.write("\nEnter task ID to delete: ")
            val taskId = console.read().trim()

            if (taskId.isBlank()) {
                console.writeError("Task ID cannot be empty")
                return
            }

            console.write("Are you sure you want to delete this task? (yes/no): ")
            val confirmation = console.read().trim().lowercase()

            if (confirmation != "yes") {
                console.write("Task deletion cancelled")
                return
            }
            val currentUser = getCurrentUserUseCase().getOrElse { e ->
                console.writeError("Failed to get current user: ${e.message}")
                return
            }
            deleteTaskUseCase(
                UUID.fromString(taskId),
                currentUser!!.id
            ).onSuccess {
                console.write("Task deleted successfully!")
            }.onFailure { e ->
                console.writeError("Failed to delete task: ${e.message}")
            }
        } catch (e: Exception) {
            console.writeError("Failed to delete task: ${e.message}")
        }
    }
}
