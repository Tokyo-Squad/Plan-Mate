package org.example.presentation

import org.example.entity.ProjectEntity
import org.example.entity.StateEntity
import org.example.logic.usecase.GetStatesByProjectId
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.example.logic.usecase.project.DeleteProjectUseCase
import org.example.logic.usecase.project.GetProjectUseCase
import org.example.logic.usecase.project.UpdateProjectUseCase
import org.example.logic.usecase.state.AddStateUseCase
import org.example.logic.usecase.state.DeleteStateUseCase
import org.example.logic.usecase.state.UpdateStateUseCase
import org.example.presentation.io.ConsoleIO
import java.util.*

class ProjectEditScreen(
    private val console: ConsoleIO,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val updateStateUseCase: UpdateStateUseCase,
    private val getProjectUseCase: GetProjectUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getStatesByProjectId: GetStatesByProjectId, // Add this
    private val addStateUseCase: AddStateUseCase,
    private val deleteStateUseCase: DeleteStateUseCase
) {
    fun show(projectId: String) {
        try {
            val project = getProjectUseCase(UUID.fromString(projectId)).getOrElse { e ->
                console.writeError("Failed to load project: ${e.message}")
                return
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
        } catch (e: Exception) {
            console.writeError("Failed to load project: ${e.message}")
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

    private fun editProjectName(project: ProjectEntity) {
        console.write("\n=== Edit Project Name ===")
        console.write("Enter new project name: ")
        val newName = console.read().trim()

        if (newName.isBlank()) {
            console.writeError("Project name cannot be empty")
            return
        }

        try {
            val currentUser = getCurrentUserUseCase().getOrElse { e ->
                console.writeError("Failed to get current user: ${e.message}")
                return
            }

            val updatedProject = project.copy(name = newName)

            updateProjectUseCase(
                projectEntity = updatedProject,
                currentUser = currentUser!!
            ).onSuccess {
                console.write("Project name updated successfully!")
            }.onFailure { e ->
                console.writeError("Failed to update project name: ${e.message}")
            }
        } catch (e: Exception) {
            console.writeError("Failed to update project name: ${e.message}")
        }
    }

    private fun editProjectStates(project: ProjectEntity) {
        while (true) {
            console.write("\n=== Edit Project States ===")

            try {
                // Get current states
                val states = getStatesByProjectId(project.id).getOrElse { e ->
                    console.writeError("Failed to get states: ${e.message}")
                    return
                }

                // Display current states
                console.write("Current states:")
                if (states.isEmpty()) {
                    console.write("(No states defined for this project)")
                } else {
                    states.forEachIndexed { index, state ->
                        console.write("${index + 1}. ${state.name}")
                    }
                }

                // Show state management options
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
            } catch (e: Exception) {
                console.writeError("Failed to manage project states: ${e.message}")
                return
            }
        }
    }

    private fun addNewState(project: ProjectEntity) {
        console.write("\n=== Add New State ===")
        console.write("Enter state name: ")
        val stateName = console.read().trim()

        if (stateName.isBlank()) {
            console.writeError("State name cannot be empty")
            return
        }

        // Create new state entity
        val newState = StateEntity(
            id = UUID.randomUUID(),
            name = stateName,
            projectId = project.id
        )

        // Add the state using the AddStateUseCase
        // Note: You'll need to inject this use case
        addStateUseCase(newState)
            .onSuccess { result ->
                result.onSuccess {
                    console.write("State added successfully!")
                }.onFailure { e ->
                    console.writeError("Failed to add state: ${e.message}")
                }
            }.onFailure { e ->
                console.writeError("Failed to add state: ${e.message}")
            }
    }

    private fun editExistingState(states: List<StateEntity>) {
        console.write("\n=== Edit Existing State ===")
        console.write("Select a state to edit (1-${states.size}): ")
        val stateIndex = console.read().toIntOrNull()?.minus(1) ?: -1

        if (stateIndex < 0 || stateIndex >= states.size) {
            console.writeError("Invalid state selection")
            return
        }

        val selectedState = states[stateIndex]
        console.write("Current name: ${selectedState.name}")
        console.write("Enter new name: ")
        val newName = console.read().trim()

        if (newName.isBlank()) {
            console.writeError("State name cannot be empty")
            return
        }

        val updatedState = selectedState.copy(name = newName)

        updateStateUseCase(selectedState, updatedState)
            .onSuccess { result ->
                result.onSuccess {
                    console.write("State updated successfully!")
                }.onFailure { e ->
                    console.writeError("Failed to update state: ${e.message}")
                }
            }.onFailure { e ->
                console.writeError("Failed to update state: ${e.message}")
            }
    }

    private fun deleteState(states: List<StateEntity>) {
        console.write("\n=== Delete State ===")
        console.write("Select a state to delete (1-${states.size}): ")
        val stateIndex = console.read().toIntOrNull()?.minus(1) ?: -1

        if (stateIndex < 0 || stateIndex >= states.size) {
            console.writeError("Invalid state selection")
            return
        }

        val selectedState = states[stateIndex]
        console.write("Are you sure you want to delete state '${selectedState.name}'? (yes/no): ")
        val confirmation = console.read().trim().lowercase()

        if (confirmation != "yes") {
            console.write("State deletion cancelled")
            return
        }

        deleteStateUseCase(selectedState)
            .onSuccess { result ->
                result.onSuccess {
                    console.write("State deleted successfully!")
                }.onFailure { e ->
                    console.writeError("Failed to delete state: ${e.message}")
                }
            }.onFailure { e ->
                console.writeError("Failed to delete state: ${e.message}")
            }
    }

    private fun handleProjectDeletion(project: ProjectEntity) {
        console.write("\n=== Delete Project ===")
        console.write("WARNING: This action cannot be undone!")
        console.write("Are you sure you want to delete this project? (yes/no): ")

        val confirmation = console.read().trim().lowercase()

        if (confirmation != "yes") {
            console.write("Project deletion cancelled")
            return
        }

        try {
            val currentUser = getCurrentUserUseCase().getOrElse { e ->
                console.writeError("Failed to get current user: ${e.message}")
                return
            }

            deleteProjectUseCase(
                projectId = project.id,
                currentUser = currentUser!!.id
            ).onSuccess {
                console.write("Project deleted successfully!")
            }.onFailure { e ->
                console.writeError("Failed to delete project: ${e.message}")
            }
        } catch (e: Exception) {
            console.writeError("Failed to delete project: ${e.message}")
        }
    }
}