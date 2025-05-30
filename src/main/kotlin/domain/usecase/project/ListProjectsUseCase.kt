package org.example.logic.usecase.project

import domain.model.Project
import org.example.logic.repository.ProjectRepository

class ListProjectsUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(): List<Project> {
        return projectRepository.getAllProjects()
    }
}