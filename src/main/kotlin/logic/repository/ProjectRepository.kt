package org.example.logic.repository

import org.example.entity.ProjectEntity
import java.util.UUID

interface ProjectRepository {

    fun addProject(project: ProjectEntity): Result<ProjectEntity>

    fun updateProject(project: ProjectEntity, currentUserId: UUID): Result<ProjectEntity>

    fun deleteProject(projectId: UUID, currentUserId: UUID): Result<Unit>

    fun getAllProjects(): Result<List<ProjectEntity>>

    fun getProjectById(projectId: String): Result<ProjectEntity>
}