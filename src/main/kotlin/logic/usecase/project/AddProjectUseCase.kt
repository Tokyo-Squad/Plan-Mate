package org.example.logic.usecase.project

import logic.model.Project
import org.example.entity.User
import org.example.entity.UserType
import org.example.logic.repository.ProjectRepository
import org.example.utils.PlanMateException

class AddProjectUseCase(
    private val projectRepository: ProjectRepository,
) {
    suspend operator fun invoke(
        project: Project,
        currentUser: User
    ): Project {

        if (currentUser.type != UserType.ADMIN) {
            throw PlanMateException.UserActionNotAllowedException(
                "User ${currentUser.id} is not authorized to create projects"
            )
        }

        if (project.name.isBlank()) {
            throw PlanMateException.ValidationException(
                "Project name cannot be blank"
            )
        }

        return projectRepository.addProject(project)
    }
}