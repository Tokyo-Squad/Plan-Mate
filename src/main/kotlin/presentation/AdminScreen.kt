import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.entity.ProjectEntity
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.example.logic.usecase.auth.RegisterUseCase
import org.example.logic.usecase.project.AddProjectUseCase
import org.example.logic.usecase.project.ListProjectsUseCase
import org.example.presentation.AuditScreen
import org.example.presentation.ProjectEditScreen
import org.example.presentation.ProjectScreen
import org.example.presentation.io.ConsoleIO

class AdminScreen(
    private val console: ConsoleIO,
    private val getProjectsUseCase: ListProjectsUseCase,
    private val createProjectUseCase: AddProjectUseCase,
    private val createUserUseCase: RegisterUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
    private val projectScreen: ProjectScreen,
    private val projectEditScreen: ProjectEditScreen,
    private val auditScreen: AuditScreen,

) {
    fun show() {
        while (true) {
            try {
                console.write("\n=== Admin Dashboard ===")
                when (showMainMenu()) {
                    1 -> handleProjects()
                    2 -> createProject()
                    3 -> createMateUser()
                    4 -> auditScreen.show()
                    5 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            } catch (e: Exception) {
                console.writeError("Error: ${e.message}")
            }
        }
    }

    private fun showMainMenu(): Int {
        console.write("1. Manage Projects")
        console.write("2. Create New Project")
        console.write("3. Create Mate User")
        console.write("4. View Audit Logs")
        console.write("5. Exit")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }

    private fun handleProjects() {
        getProjectsUseCase().fold(onSuccess = { projects ->
            if (projects.isEmpty()) {
                console.write("\nNo projects found.")
                return
            }

            while (true) {
                displayProjects(projects)

                when (showProjectsMenu()) {
                    1 -> viewProject()
                    2 -> editProject()
                    3 -> return
                    else -> console.writeError("Invalid option. Please try again.")
                }
            }
        }, onFailure = { error ->
            console.writeError("Failed to load projects: ${error.message}")
            console.writeError(error.printStackTrace().toString())
        })
    }

    private fun displayProjects(projects: List<ProjectEntity>) {
        console.write("\n=== Projects ===")
        projects.forEach { project ->
            console.write("${project.id}: ${project.name}")
        }
    }

    private fun showProjectsMenu(): Int {
        console.write("\n1. View Project Tasks")
        console.write("2. Edit Project")
        console.write("3. Back")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
    }

    private fun viewProject() {
        console.write("Enter project ID: ")
        val projectId = console.read().trim()

        if (projectId.isBlank()) {
            console.writeError("Project ID cannot be empty")
            return
        }

        try {
            projectScreen.show(projectId)
        } catch (e: Exception) {
            console.writeError("Failed to open project: ${e.message}")
        }
    }

    private fun editProject() {
        console.write("Enter project ID: ")
        val projectId = console.read().trim()

        if (projectId.isBlank()) {
            console.writeError("Project ID cannot be empty")
            return
        }

        try {
            projectEditScreen.show(projectId)
        } catch (e: Exception) {
            console.writeError("Failed to edit project: ${e.message}")
        }
    }


    private fun createProject() {
        console.write("\n=== Create New Project ===")
        console.write("Enter project name: ")
        val projectName = console.read().trim()

        if (projectName.isBlank()) {
            console.writeError("Project name cannot be empty")
            return
        }
        val currentUser = getCurrentUser().getOrNull()
        if (currentUser != null) {
            createProjectUseCase(
                ProjectEntity(
                    name = projectName,
                    createdByAdminId = currentUser.id,
                    createdAt = Clock.System.now().toLocalDateTime(
                        TimeZone.currentSystemDefault()
                    )
                ), currentUser
            ).fold(onSuccess = {
                console.write("Project created successfully!")
            }, onFailure = { error ->
                console.writeError("Failed to create project: ${error.message}")
            })
        } else {
            console.writeError("Failed to get current user")
        }
    }

    private fun createMateUser() {
        console.write("\n=== Create Mate User ===")

        console.write("Enter username: ")
        val username = console.read().trim()

        if (username.isBlank()) {
            console.writeError("Username cannot be empty")
            return
        }

        console.write("Enter password: ")
        val password = console.read().trim()

        if (password.isBlank()) {
            console.writeError("Password cannot be empty")
            return
        }

        getCurrentUser().fold(onSuccess = { currentUser ->
            createUserUseCase(
                newUser = UserEntity(
                    username = username, password = password, type = UserType.MATE
                ), currentUser = currentUser!!
            ).fold(onSuccess = {
                console.write("Mate user created successfully!")
            }, onFailure = { error ->
                console.writeError("Failed to create user: ${error.message}")
            })
        }, onFailure = { error ->
            console.writeError("Failed to get current user: ${error.message}")
        })
    }
}