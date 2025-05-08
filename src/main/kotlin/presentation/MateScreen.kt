import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.entity.ProjectEntity
import org.example.logic.usecase.project.ListProjectsUseCase
import org.example.presentation.AuditScreen
import org.example.presentation.ProjectScreen
import org.example.presentation.io.ConsoleIO
import org.example.utils.PlanMateException
import kotlin.coroutines.cancellation.CancellationException


class MateScreen(
    private val console: ConsoleIO,
    private val getProjectsUseCase: ListProjectsUseCase,
    private val projectScreen: ProjectScreen,
    private val auditScreen: AuditScreen
) {
    private enum class MainMenuOption {
        VIEW_PROJECTS, VIEW_AUDIT_LOGS, LOGOUT, INVALID
    }

    suspend fun show() {
        while (true) {
            try {
                displayMainMenu()
                when (getMenuSelection()) {
                    MainMenuOption.VIEW_PROJECTS -> handleProjects()
                    MainMenuOption.VIEW_AUDIT_LOGS -> withContext(Dispatchers.IO) {
                        auditScreen.show()
                    }
                    MainMenuOption.LOGOUT -> return
                    MainMenuOption.INVALID -> console.writeError("Invalid option. Please try again.")
                }
            } catch (e: CancellationException) {
                return
            } catch (e: PlanMateException) {
                console.writeError("Operation failed: ${e.message}")
            } catch (e: Exception) {
                console.writeError("An unexpected error occurred: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun displayMainMenu() {
        console.write("\n=== Mate Dashboard ===")
        console.write("1. View Projects")
        console.write("2. View Audit Logs")
        console.write("3. Logout")
    }

    private fun getMenuSelection(): MainMenuOption {
        console.write("\nSelect an option: ")
        return when (console.read().toIntOrNull()) {
            1 -> MainMenuOption.VIEW_PROJECTS
            2 -> MainMenuOption.VIEW_AUDIT_LOGS
            3 -> MainMenuOption.LOGOUT
            else -> MainMenuOption.INVALID
        }
    }

    private suspend fun handleProjects() {
        val projects = try {
            withContext(Dispatchers.IO) {
                getProjectsUseCase.invoke()
            }
        } catch (e: PlanMateException) {
            console.writeError("Failed to load projects: ${e.message}")
            return
        } catch (e: Exception) {
            console.writeError("Unexpected error loading projects: ${e.message}")
            e.printStackTrace()
            return
        }

        if (projects.isEmpty()) {
            console.write("\nNo projects available.")
            return
        }

        while (true) {
            displayProjects(projects)
            when (val input = promptForProjectSelection()) {
                "back" -> return
                else -> handleSelectedProject(input, projects)
            }
        }
    }

    private fun displayProjects(projects: List<ProjectEntity>) {
        console.write("\n=== Available Projects ===")
        projects.forEachIndexed { index, project ->
            console.write("${index + 1}. ${project.name} (ID: ${project.id})")
        }
    }

    private fun promptForProjectSelection(): String {
        console.write("\nEnter project number to view details (or 'back' to return): ")
        return console.read().trim()
    }

    private suspend fun handleSelectedProject(input: String, projects: List<ProjectEntity>) {
        try {
            val projectIndex = input.toInt() - 1
            if (projectIndex in projects.indices) {
                withContext(Dispatchers.IO) {
                    projectScreen.show(projects[projectIndex].id.toString())
                }
            } else {
                console.writeError("Please enter a number between 1 and ${projects.size}")
            }
        } catch (e: NumberFormatException) {
            console.writeError("Invalid input. Please enter a number or 'back'")
        } catch (e: PlanMateException) {
            console.writeError("Failed to view project: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error viewing project: ${e.message}")
            e.printStackTrace()
        }
    }
}