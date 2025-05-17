package org.example.logic.repository

import domain.model.Project
import java.util.UUID

interface ProjectRepository {

    suspend fun addProject(project: Project): Project

    suspend fun updateProject(project: Project, currentUserId: UUID): Project

    suspend fun deleteProject(projectId: UUID, currentUserId: UUID)

    suspend fun getAllProjects(): List<Project>

    suspend fun getProjectById(projectId: UUID): Project
}