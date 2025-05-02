package org.example.logic.usecase.user

import org.example.entity.ProjectEntity
import org.example.logic.repository.ProjectRepository

class ListProjectsUseCase(
    private val projectRepository: ProjectRepository
) {
    operator fun invoke(): Result<List<ProjectEntity>> {
        return projectRepository.getAllProjects()
    }
}