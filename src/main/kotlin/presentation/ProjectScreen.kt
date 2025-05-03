package org.example.presentation

import org.example.logic.usecase.project.GetProjectUseCase
import org.example.presentation.io.ConsoleIO
import java.util.*

class ProjectScreen(
    private val console: ConsoleIO,
    private val getProjectUseCase: GetProjectUseCase,
    private val taskEditScreen: TaskEditScreen
) {
    fun show(projectId: String) {
        try {
            val projectResult = getProjectUseCase.invoke(projectId = UUID.fromString(projectId))
            val project = projectResult.getOrElse { e ->
                console.writeError("Failed to load project: ${e.message}")
                return
            }

            while (true) {
                console.write("\n=== Project: ${project.name} ===")
                when (showProjectMenu()) {
                    1 -> taskEditScreen.showTasksForProject(project.id)
                    2 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            }
        } catch (e: Exception) {
            console.writeError("Failed to load project: ${e.message}")
        }
    }

    private fun showProjectMenu(): Int {
        console.write("1. Manage Tasks")
        console.write("2. Back")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }
}