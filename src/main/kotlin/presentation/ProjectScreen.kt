package org.example.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.logic.usecase.project.GetProjectUseCase
import org.example.presentation.io.ConsoleIO
import org.example.utils.PlanMateException
import java.util.*


class ProjectScreen(
    private val console: ConsoleIO,
    private val getProjectUseCase: GetProjectUseCase,
    private val taskEditScreen: TaskEditScreen
) {
    suspend fun show(projectId: String) {
        try {
            val project = withContext(Dispatchers.IO) {
                getProjectUseCase.invoke(UUID.fromString(projectId))
            }

            while (true) {
                console.write("\n=== Project: ${project.name} (ID: ${project.id}) ===")
                when (showProjectMenu()) {
                    1 -> withContext(Dispatchers.IO) {
                        taskEditScreen.showTasksForProject(project.id)
                    }
                    2 -> return
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

    private fun showProjectMenu(): Int {
        console.write("1. Manage Tasks")
        console.write("2. Back")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }
}