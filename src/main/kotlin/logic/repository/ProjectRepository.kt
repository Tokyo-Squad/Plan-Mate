package org.example.logic.repository

import org.example.entity.ProjectEntity
import java.util.UUID

interface ProjectRepository {

    suspend fun addProject(project: ProjectEntity): ProjectEntity

    suspend fun updateProject(project: ProjectEntity, currentUserId: UUID): ProjectEntity

    suspend fun deleteProject(projectId: UUID, currentUserId: UUID)

    suspend fun getAllProjects(): List<ProjectEntity>

    suspend fun getProjectById(projectId: String): ProjectEntity
}