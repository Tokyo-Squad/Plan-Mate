package org.example.logic.usecase.user

import org.example.entity.ProjectEntity
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.ProjectRepository
import org.example.utils.PlanMatException

class UpdateProjectUseCase(
    private val projectRepository: ProjectRepository,
) {
    operator fun invoke(
        projectEntity: ProjectEntity,
        currentUser: UserEntity
    ): Result<ProjectEntity> {
        if (currentUser.type != UserType.ADMIN) return Result.failure(PlanMatException.UserActionNotAllowedException())
        if (projectEntity.name.isBlank()) return Result.failure(PlanMatException.ValidationException())

        return runCatching {
            projectRepository.updateProject(projectEntity, currentUser.id).getOrThrow()
        }
    }
}