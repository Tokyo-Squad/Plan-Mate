import org.example.logic.usecase.project.ListProjectsUseCase
import org.example.presentation.AuditScreen
import org.example.presentation.ProjectScreen
import org.example.presentation.io.ConsoleIO

class MateScreen(
    private val console: ConsoleIO,
    private val getProjectsUseCase: ListProjectsUseCase,
    private val projectScreen: ProjectScreen,
    private val auditScreen: AuditScreen
) {
    fun show() {
        while (true) {
            try {
                console.write("\n=== Mate Dashboard ===")
                when (showMainMenu()) {
                    1 -> handleProjects()
                    2 -> auditScreen.show()
                    3 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            } catch (e: Exception) {
                console.writeError("Error: ${e.message}")
            }
        }
    }

    private fun showMainMenu(): Int {
        console.write("1. View Projects")
        console.write("2. View Audit Logs")
        console.write("3. Logout")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }

    private fun handleProjects() {
        try {
            val projectsResult = getProjectsUseCase() // Returns Result<List<ProjectEntity>>
            val projects = projectsResult.getOrElse { exception ->
                console.writeError("Failed to load projects: ${exception.message}")
                return
            }

            if (projects.isEmpty()) {
                console.writeError("\nNo projects available.")
                return
            }

            while (true) {
                console.write("\n=== Available Projects ===")
                projects.forEach { project ->
                    console.write("${project.id}: ${project.name}")
                }

                console.write("\nEnter project ID to view details (or 'back' to return): ")
                val input = console.read()

                if (input.equals("back", ignoreCase = true)) {
                    return
                }

                try {
                    projectScreen.show(input)
                } catch (e: Exception) {
                    console.writeError("Invalid project ID")
                }
            }
        } catch (e: Exception) {
            console.writeError("Failed to load projects: ${e.message}")
        }
    }

}