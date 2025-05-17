import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logic.model.Project
import org.example.logic.usecase.project.ListProjectsUseCase
import org.example.presentation.AuditScreen
import org.example.presentation.ProjectScreen
import org.example.presentation.io.ConsoleIO
import org.example.utils.PlanMateException

class MateScreen(
    private val console: ConsoleIO,
    private val getProjectsUseCase: ListProjectsUseCase,
    private val projectScreen: ProjectScreen,
    private val auditScreen: AuditScreen
) {
    suspend fun show() {
        while (true) {
            try {
                console.write("\n=== Mate Dashboard ===")
                when (showMainMenu()) {
                    1 -> handleProjects()
                    2 -> auditScreen.show()
                    3 -> return
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

    private fun showMainMenu(): Int {
        console.write("1. View Projects")
        console.write("2. View Audit Logs")
        console.write("3. Exit")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }

    private suspend fun handleProjects() {
        try {
            val projects = withContext(Dispatchers.IO) {
                getProjectsUseCase.invoke()
            }

            if (projects.isEmpty()) {
                console.write("\nNo projects found.")
                return
            }

            while (true) {
                displayProjects(projects)

                when (showProjectsMenu()) {
                    1 -> viewProject(projects)
                    2 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            }
        } catch (error: PlanMateException) {
            console.writeError("Operation failed: ${error.message}")
        } catch (error: Exception) {
            console.writeError("Unexpected error: ${error.message}")
            error.printStackTrace()
        }
    }

    private fun showProjectsMenu(): Int {
        console.write("\n1. View Project Tasks")
        console.write("2. Back")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }

    private fun displayProjects(projects: List<Project>) {
        console.write("\n=== Projects ===")
        projects.forEachIndexed { index, project ->
            console.write("${project.name} (ID: ${project.id})")
        }
    }

    private suspend fun viewProject(projects: List<Project>) {
        console.write("\nAvailable projects:")
        projects.forEachIndexed { index, project ->
            console.write("${index + 1}. ${project.name} (ID: ${project.id})")
        }

        console.write("\nEnter project number (1-${projects.size}): ")
        val projectNumber = console.read().toIntOrNull()

        if (projectNumber == null || projectNumber < 1 || projectNumber > projects.size) {
            console.writeError("Invalid project number")
            return
        }

        try {
            val selectedProject = projects[projectNumber - 1]
            withContext(Dispatchers.IO) {
                projectScreen.show(selectedProject.id.toString())
            }
        } catch (e: PlanMateException) {
            console.writeError("Failed to open project: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error: ${e.message}")
            e.printStackTrace()
        }
    }
}