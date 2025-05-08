import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
import org.example.utils.PlanMateException

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
    suspend fun show() {
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
            } catch (e: PlanMateException) {
                console.writeError("Operation failed: ${e.message}")
            } catch (e: Exception) {
                console.writeError("Unexpected error: ${e.message}")
                e.printStackTrace()
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
                    1 -> viewProject()
                    2 -> editProject()
                    3 -> return
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

    private suspend fun viewProject() {
        console.write("Enter project ID: ")
        val projectId = console.read().trim()

        if (projectId.isBlank()) {
            console.writeError("Project ID cannot be empty")
            return
        }

        try {
            withContext(Dispatchers.IO) {
                projectScreen.show(projectId)
            }
        } catch (e: PlanMateException) {
            console.writeError("Failed to open project: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun editProject() {
        console.write("Enter project ID: ")
        val projectId = console.read().trim()

        if (projectId.isBlank()) {
            console.writeError("Project ID cannot be empty")
            return
        }

        try {
            withContext(Dispatchers.IO) {
                projectEditScreen.show(projectId)
            }
        } catch (e: PlanMateException) {
            console.writeError("Failed to edit project: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun createProject() {
        try {
            console.write("\n=== Create New Project ===")
            console.write("Enter project name: ")
            val projectName = console.read().trim()

            if (projectName.isBlank()) {
                throw PlanMateException.ValidationException("Project name cannot be empty")
            }

            val currentUser = withContext(Dispatchers.IO) {
                getCurrentUser.invoke()
            } ?: throw PlanMateException.UserActionNotAllowedException("Not authenticated")

            val newProject = ProjectEntity(
                name = projectName,
                createdByAdminId = currentUser.id,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )

            withContext(Dispatchers.IO) {
                createProjectUseCase.invoke(newProject, currentUser)
            }

            console.write("Project created successfully!")
        } catch (e: PlanMateException) {
            console.writeError("Failed to create project: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun createMateUser() {
        try {
            console.write("\n=== Create Mate User ===")

            console.write("Enter username: ")
            val username = console.read().trim()

            if (username.isBlank()) {
                throw PlanMateException.ValidationException("Username cannot be empty")
            }

            console.write("Enter password: ")
            val password = console.read().trim()

            if (password.isBlank()) {
                throw PlanMateException.ValidationException("Password cannot be empty")
            }

            val currentUser = withContext(Dispatchers.IO) {
                getCurrentUser.invoke()
            } ?: throw PlanMateException.UserActionNotAllowedException("Not authenticated")

            val newUser = UserEntity(
                username = username,
                password = password,
                type = UserType.MATE
            )

            withContext(Dispatchers.IO) {
                createUserUseCase.invoke(newUser, currentUser)
            }

            console.write("Mate user created successfully!")
        } catch (e: PlanMateException) {
            console.writeError("Failed to create user: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error: ${e.message}")
            e.printStackTrace()
        }
    }
}