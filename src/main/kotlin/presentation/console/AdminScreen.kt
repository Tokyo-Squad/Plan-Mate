package presentation.console

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import domain.model.Project
import org.example.entity.User
import org.example.entity.UserType
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.example.logic.usecase.auth.RegisterUseCase
import org.example.logic.usecase.project.AddProjectUseCase
import org.example.logic.usecase.project.ListProjectsUseCase
import org.example.presentation.io.ConsoleIO
import domain.utils.exception.PlanMateException

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



    private fun showProjectsMenu(): Int {
        console.write("\n1. View Project Tasks")
        console.write("2. Edit Project")
        console.write("3. Back")
        console.write("\nSelect an option: ")
        return console.read().toIntOrNull() ?: 0
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

            val newProject = Project(
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

            val newUser = User(
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
                    2 -> editProject(projects)
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

    private fun displayProjects(projects: List<Project>) {
        console.write("\n=== Projects ===")
        projects.forEachIndexed { index, project ->
            console.write("${project.name} (ID: ${project.id})")
        }
    }

    private suspend fun viewProject(projects: List<Project>) {
        // First display numbered projects for selection
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

    private suspend fun editProject(projects: List<Project>) {
        // First display numbered projects for selection
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
                projectEditScreen.show(selectedProject.id.toString())
            }
        } catch (e: PlanMateException) {
            console.writeError("Failed to edit project: ${e.message}")
        } catch (e: Exception) {
            console.writeError("Unexpected error: ${e.message}")
            e.printStackTrace()
        }
    }
}