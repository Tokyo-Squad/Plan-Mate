package org.example.logic.usecase.project

import org.example.entity.ProjectEntity
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.ProjectRepository
import org.example.utils.PlanMateException

class UpdateProjectUseCase(
    private val projectRepository: ProjectRepository,
) {
    operator fun invoke(
        projectEntity: ProjectEntity,
        currentUser: UserEntity
    ): Result<ProjectEntity> {
        if (currentUser.type != UserType.ADMIN) return Result.failure(PlanMateException.UserActionNotAllowedException())
        if (projectEntity.name.isBlank()) return Result.failure(PlanMateException.ValidationException())

        return runCatching {
            projectRepository.updateProject(projectEntity, currentUser.id).getOrThrow()
        }
    }
}