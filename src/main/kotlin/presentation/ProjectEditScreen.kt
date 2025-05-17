package org.example.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logic.model.Project
import logic.model.WorkflowState
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.example.logic.usecase.project.DeleteProjectUseCase
import org.example.logic.usecase.project.GetProjectUseCase
import org.example.logic.usecase.project.UpdateProjectUseCase
import org.example.logic.usecase.state.AddStateUseCase
import org.example.logic.usecase.state.DeleteStateUseCase
import org.example.logic.usecase.state.GetStatesByProjectId
import org.example.logic.usecase.state.UpdateStateUseCase
import org.example.presentation.io.ConsoleIO
import org.example.utils.PlanMateException
import java.util.*


class ProjectEditScreen(
    private val console: ConsoleIO,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val updateStateUseCase: UpdateStateUseCase,
    private val getProjectUseCase: GetProjectUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getStatesByProjectId: GetStatesByProjectId,
    private val addStateUseCase: AddStateUseCase,
    private val deleteStateUseCase: DeleteStateUseCase
) {
    suspend fun show(projectId: String) {
        try {
            val project = withContext(Dispatchers.IO) {
                getProjectUseCase.invoke(UUID.fromString(projectId))
            }

            while (true) {
                console.write("\n=== Edit Project: ${project.name} ===")
                when (showMenu()) {
                    1 -> editProjectName(project)
                    2 -> editProjectStates(project)
                    3 -> handleProjectDeletion(project)
                    4 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            }
        } catch (e: PlanMateException) {
            console.writeError("Failed to load project: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error loading project: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun showMenu(): Int {
        console.write("1. Edit Project Name")
        console.write("2. Edit Project States")
        console.write("3. Delete Project")
        console.write("4. Back")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }

    private suspend fun editProjectName(project: Project) {
        console.write("\n=== Edit Project Name ===")
        console.write("Enter new project name: ")
        val newName = console.read().trim()

        if (newName.isBlank()) {
            console.writeError("Project name cannot be empty")
            return
        }

        try {
            val currentUser = withContext(Dispatchers.IO) {
                getCurrentUserUseCase.invoke()
            } ?: throw PlanMateException.UserActionNotAllowedException("Not authenticated")

            val updatedProject = project.copy(name = newName)

            withContext(Dispatchers.IO) {
                updateProjectUseCase.invoke(updatedProject, currentUser)
            }

            console.write("Project name updated successfully!")
        } catch (e: PlanMateException) {
            console.writeError("Failed to update project name: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error updating project name: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun editProjectStates(project: Project) {
        while (true) {
            console.write("\n=== Edit Project States ===")

            try {
                val states = withContext(Dispatchers.IO) {
                    getStatesByProjectId.invoke(project.id)
                }

                console.write("Current states:")
                if (states.isEmpty()) {
                    console.write("(No states defined for this project)")
                } else {
                    states.forEachIndexed { index, state ->
                        console.write("${index + 1}. ${state.name}")
                    }
                }

                console.write("\nOptions:")
                console.write("1. Add new state")
                console.write("2. Edit existing state")
                console.write("3. Delete state")
                console.write("4. Back to project menu")
                console.write("\nSelect an option: ")

                when (console.read().toIntOrNull() ?: 0) {
                    1 -> addNewState(project)
                    2 -> {
                        if (states.isEmpty()) {
                            console.writeError("No states to edit")
                            continue
                        }
                        editExistingState(states)
                    }
                    3 -> {
                        if (states.isEmpty()) {
                            console.writeError("No states to delete")
                            continue
                        }
                        deleteState(states)
                    }
                    4 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            } catch (e: PlanMateException) {
                console.writeError("Failed to manage project states: ${e.message}")
                return
            } catch (e: Exception) {
                console.writeError("Unexpected error managing project states: ${e.message}")
                e.printStackTrace()
                return
            }
        }
    }

    private suspend fun addNewState(project: Project) {
        console.write("\n=== Add New State ===")
        console.write("Enter state name: ")
        val stateName = console.read().trim()

        if (stateName.isBlank()) {
            console.writeError("State name cannot be empty")
            return
        }

        val newWorkflowState = WorkflowState(
            id = UUID.randomUUID(),
            name = stateName,
            projectId = project.id
        )

        try {
            withContext(Dispatchers.IO) {
                addStateUseCase.invoke(newWorkflowState)
            }
            console.write("State added successfully!")
        } catch (e: PlanMateException) {
            console.writeError("Failed to add state: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error adding state: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun editExistingState(workflowStates: List<WorkflowState>) {
        console.write("\n=== Edit Existing State ===")
        console.write("Select a state to edit (1-${workflowStates.size}): ")
        val stateIndex = console.read().toIntOrNull()?.minus(1) ?: -1

        if (stateIndex < 0 || stateIndex >= workflowStates.size) {
            console.writeError("Invalid state selection")
            return
        }

        val selectedState = workflowStates[stateIndex]
        console.write("Current name: ${selectedState.name}")
        console.write("Enter new name: ")
        val newName = console.read().trim()

        if (newName.isBlank()) {
            console.writeError("State name cannot be empty")
            return
        }

        val updatedState = selectedState.copy(name = newName)

        try {
            withContext(Dispatchers.IO) {
                updateStateUseCase.invoke(selectedState, updatedState)
            }
            console.write("State updated successfully!")
        } catch (e: PlanMateException) {
            console.writeError("Failed to update state: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error updating state: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun deleteState(workflowStates: List<WorkflowState>) {
        console.write("\n=== Delete State ===")
        console.write("Select a state to delete (1-${workflowStates.size}): ")
        val stateIndex = console.read().toIntOrNull()?.minus(1) ?: -1

        if (stateIndex < 0 || stateIndex >= workflowStates.size) {
            console.writeError("Invalid state selection")
            return
        }

        val selectedState = workflowStates[stateIndex]
        console.write("Are you sure you want to delete state '${selectedState.name}'? (yes/no): ")
        val confirmation = console.read().trim().lowercase()

        if (confirmation != "yes") {
            console.write("State deletion cancelled")
            return
        }

        try {
            withContext(Dispatchers.IO) {
                deleteStateUseCase.invoke(selectedState)
            }
            console.write("State deleted successfully!")
        } catch (e: PlanMateException) {
            console.writeError("Failed to delete state: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error deleting state: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun handleProjectDeletion(project: Project) {
        console.write("\n=== Delete Project ===")
        console.write("WARNING: This action cannot be undone!")
        console.write("Are you sure you want to delete this project? (yes/no): ")

        val confirmation = console.read().trim().lowercase()

        if (confirmation != "yes") {
            console.write("Project deletion cancelled")
            return
        }

        try {
            val currentUser = withContext(Dispatchers.IO) {
                getCurrentUserUseCase.invoke()
            } ?: throw PlanMateException.UserActionNotAllowedException("Not authenticated")

            withContext(Dispatchers.IO) {
                deleteProjectUseCase.invoke(project.id, currentUser.id)
            }

            console.write("Project deleted successfully!")
        } catch (e: PlanMateException) {
            console.writeError("Failed to delete project: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error deleting project: ${e.message}")
            e.printStackTrace()
        }
    }
}