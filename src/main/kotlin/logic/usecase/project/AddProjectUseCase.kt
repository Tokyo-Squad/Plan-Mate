package org.example.logic.usecase.project

import org.example.entity.ProjectEntity
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.ProjectRepository
import org.example.utils.PlanMateException

class AddProjectUseCase(
    private val projectRepository: ProjectRepository,
) {
    suspend operator fun invoke(
        projectEntity: ProjectEntity,
        currentUser: UserEntity
    ): ProjectEntity {
        if (currentUser.type != UserType.ADMIN) {
            throw PlanMateException.UserActionNotAllowedException(
                "User ${currentUser.id} is not authorized to create projects"
            )
        }

        if (projectEntity.name.isBlank()) {
            throw PlanMateException.ValidationException(
                "Project name cannot be blank"
            )
        }

        return projectRepository.addProject(projectEntity)
    }
}