package org.example.presentation

import SwimlaneRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import domain.model.Task
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.example.logic.usecase.project.GetProjectUseCase
import org.example.logic.usecase.state.GetStatesByProjectId
import org.example.logic.usecase.task.*
import org.example.presentation.io.ConsoleIO
import domain.utils.exception.PlanMateException
import java.util.*


class TaskEditScreen(
    private val console: ConsoleIO,
    private val getTaskUseCase: GetTaskByIdUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getTasksByProjectUseCase: GetTasksByProjectIdUseCase,
    private val createTaskUseCase: AddTaskUseCase,
    private val getStatesByProjectId: GetStatesByProjectId,
    private val getProjectUseCase: GetProjectUseCase,
    private val swimlaneRenderer: SwimlaneRenderer
) {
    private suspend fun show(taskId: String) {
        while (true) {
            try {
                console.write("\n=== Edit Task ===")
                when (showMenu()) {
                    1 -> editTaskTitle(taskId)
                    2 -> editTaskDescription(taskId)
                    3 -> updateTaskStatus(UUID.fromString(taskId))
                    4 -> handleTaskDeletion(taskId)
                    5 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            } catch (e: PlanMateException) {
                console.writeError("Operation failed: ${e.message}")
            } catch (e: Exception) {
                console.writeError("Unexpected error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    suspend fun showTasksForProject(projectId: UUID) {
        try {
            val project = withContext(Dispatchers.IO) {
                getProjectUseCase.invoke(projectId)
            }

            while (true) {
                console.write("\n=== Tasks for Project: ${project.name} ===")
                when (showProjectTasksMenu()) {
                    1 -> viewTasks(projectId)
                    2 -> createTask(projectId)
                    3 -> editSpecificTask()
                    4 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            }
        } catch (e: PlanMateException) {
            console.writeError("Operation failed: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error: ${e.message}")
            e.printStackTrace()
        }
    }


    private fun showProjectTasksMenu(): Int {
        console.write("1. View Tasks")
        console.write("2. Create Task")
        console.write("3. Edit Task")
        console.write("4. Back")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }

    // Existing methods
    private fun showMenu(): Int {
        console.write("1. Edit Title")
        console.write("2. Edit Description")
        console.write("3. Update Status")
        console.write("4. Delete Task")
        console.write("5. Back")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }

    private suspend fun editTaskTitle(taskId: String) {
        console.write("Enter new title: ")
        val newTitle = console.read().trim()

        if (newTitle.isBlank()) {
            console.writeError("Title cannot be empty")
            return
        }

        try {
            val taskUUID = UUID.fromString(taskId)

            // Get current task
            val currentTask = withContext(Dispatchers.IO) {
                getTaskUseCase.invoke(taskUUID)
            }

            // Get current user
            val currentUser = withContext(Dispatchers.IO) {
                getCurrentUserUseCase.invoke()
            } ?: throw PlanMateException.UserActionNotAllowedException("Not authenticated")

            // Create updated task
            val updatedTask = currentTask.copy(
                title = newTitle
            )

            // Update task
            withContext(Dispatchers.IO) {
                updateTaskUseCase.invoke(updatedTask, currentUser.id)
            }

            console.write("Task title updated successfully!")
        } catch (e: PlanMateException) {
            console.writeError("Failed to update task title: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error updating task title: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun editTaskDescription(taskId: String) {
        console.write("Enter new description: ")
        val newDescription = console.read().trim()

        if (newDescription.isBlank()) {
            console.writeError("Description cannot be empty")
            return
        }

        try {
            val taskUUID = UUID.fromString(taskId)

            // Get current task
            val currentTask = withContext(Dispatchers.IO) {
                getTaskUseCase.invoke(taskUUID)
            }

            // Get current user
            val currentUser = withContext(Dispatchers.IO) {
                getCurrentUserUseCase.invoke()
            } ?: throw PlanMateException.UserActionNotAllowedException("Not authenticated")

            // Create updated task
            val updatedTask = currentTask.copy(
                description = newDescription
            )

            // Update task
            withContext(Dispatchers.IO) {
                updateTaskUseCase.invoke(updatedTask, currentUser.id)
            }

            console.write("Task description updated successfully!")
        } catch (e: PlanMateException) {
            console.writeError("Failed to update task description: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error updating task description: ${e.message}")
        }
    }

    private suspend fun handleTaskDeletion(taskId: String) {
        console.write("Are you sure you want to delete this task? (yes/no): ")
        val confirmation = console.read().trim().lowercase()

        if (confirmation != "yes") {
            console.write("Task deletion cancelled")
            return
        }

        try {
            val taskUUID = UUID.fromString(taskId)

            // Get current user
            val currentUser = withContext(Dispatchers.IO) {
                getCurrentUserUseCase.invoke()
            } ?: throw PlanMateException.UserActionNotAllowedException("Not authenticated")

            // Delete task
            withContext(Dispatchers.IO) {
                deleteTaskUseCase.invoke(taskUUID, currentUser.id)
            }

            console.write("Task deleted successfully!")
        } catch (e: PlanMateException) {
            console.writeError("Failed to delete task: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error deleting task: ${e.message}")
            e.printStackTrace()
        }
    }

    // New methods moved from ProjectScreen
    private suspend fun viewTasks(projectId: UUID) {
        try {
            val tasks = withContext(Dispatchers.IO) {
                getTasksByProjectUseCase.invoke(projectId)
            }

            val project = withContext(Dispatchers.IO) {
                getProjectUseCase.invoke(projectId)
            }

            val states = withContext(Dispatchers.IO) {
                getStatesByProjectId.invoke(project.id)
            }

            val stateMap = states.associateBy({ it.id }, { it.name })
            swimlaneRenderer.render(tasks, stateMap)
        } catch (e: PlanMateException) {
            console.writeError("Failed to load tasks: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error loading tasks: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun createTask(projectId: UUID) {
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
            val currentUser = withContext(Dispatchers.IO) {
                getCurrentUserUseCase.invoke()
            } ?: throw PlanMateException.UserActionNotAllowedException("Not authenticated")

            // Get initial state for the project
            val states = withContext(Dispatchers.IO) {
                getStatesByProjectId.invoke(projectId)
            }

            // Assuming the first state is the initial state
            val initialState = states.firstOrNull()?.id ?: run {
                console.writeError("No states found for this project")
                return
            }

            val newTask = Task(
                title = title,
                description = description,
                workflowStateId = initialState,
                projectId = projectId,
                createdByUserId = currentUser.id,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )

            withContext(Dispatchers.IO) {
                createTaskUseCase.invoke(newTask, currentUser.id)
            }

            console.write("Task created successfully!")
        } catch (e: PlanMateException) {
            console.writeError("Failed to create task: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error creating task: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun editSpecificTask() {
        console.write("Enter task ID: ")
        val taskId = console.read().trim()

        if (taskId.isBlank()) {
            console.writeError("Task ID cannot be empty")
            return
        }

        try {
            show(taskId)
        } catch (e: PlanMateException) {
            console.writeError("Failed to edit task: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error editing task: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun updateTaskStatus(taskId: UUID) {
        try {
            // Get current task
            val task = withContext(Dispatchers.IO) {
                getTaskUseCase.invoke(taskId)
            }

            // Get states for the task's project
            val states = withContext(Dispatchers.IO) {
                getStatesByProjectId.invoke(task.projectId)
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
            val currentUser = withContext(Dispatchers.IO) {
                getCurrentUserUseCase.invoke()
            } ?: throw PlanMateException.UserActionNotAllowedException("Not authenticated")

            // Create updated task with new state
            val updatedTask = task.copy(
                workflowStateId = states[stateIndex].id
            )

            // Update the task
            withContext(Dispatchers.IO) {
                updateTaskUseCase.invoke(updatedTask, currentUser.id)
            }

            console.write("Task status updated successfully!")
        } catch (e: PlanMateException) {
            console.writeError("Failed to update task status: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error updating task status: ${e.message}")
            e.printStackTrace()
        }
    }
}