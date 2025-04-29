package org.example.logic.repository

import org.example.entity.ProjectEntity
import java.util.UUID

interface ProjectsRepository {

    fun createProject(project: ProjectEntity, currentUser: String): Result<ProjectEntity>

    fun updateProject(project: ProjectEntity, currentUser: String): Result<ProjectEntity>

    fun deleteProject(projectId: UUID, currentUser: String): Result<Unit>

    fun getAllProjects(): Result<List<ProjectEntity>>

    fun getProjectById(projectId: String): Result<ProjectEntity>
}