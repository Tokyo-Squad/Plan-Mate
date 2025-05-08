package org.example.logic.usecase.project

import org.example.entity.ProjectEntity
import org.example.logic.repository.ProjectRepository

class ListProjectsUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(): List<ProjectEntity> {
        return projectRepository.getAllProjects()
    }
}