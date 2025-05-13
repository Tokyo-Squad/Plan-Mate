package org.example.data.repository

import org.example.data.DataProvider
import org.example.data.logAudit
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.ProjectEntity
import org.example.logic.repository.ProjectRepository
import java.util.*

class ProjectRepositoryImpl(
    private val projectDataProvider: DataProvider<ProjectEntity>,
    private val auditDataProvider: DataProvider<AuditLogEntity>
) : ProjectRepository {

    override suspend fun addProject(project: ProjectEntity): ProjectEntity {
        require(project.name.isNotBlank()) { "Project name cannot be blank" }

        projectDataProvider.add(project)
        logAudit(
            userId = project.createdByAdminId,
            entityId = project.id,
            action = AuditAction.CREATE,
            changeDetails = "Created project: ${project.name}",
            auditDataProvider
        )
        return project
    }

    override suspend fun updateProject(project: ProjectEntity, currentUserId: UUID): ProjectEntity {
        projectDataProvider.update(project)

        logAudit(
            userId = currentUserId,
            entityId = project.id,
            action = AuditAction.UPDATE,
            changeDetails = "Updated project: ${project.name}", auditDataProvider
        )
        return project
    }

    override suspend fun deleteProject(projectId: UUID, currentUserId: UUID) {
        val project = projectDataProvider.getById(projectId)
            ?: throw NoSuchElementException("Project not found")
        projectDataProvider.delete(projectId)

        logAudit(
            userId = currentUserId,
            entityId = projectId,
            action = AuditAction.DELETE,
            changeDetails = "Deleted project: ${project.name}",
            auditDataProvider
        )
    }

    override suspend fun getAllProjects(): List<ProjectEntity> = projectDataProvider.get()

    override suspend fun getProjectById(projectId: UUID): ProjectEntity {
        return projectDataProvider.getById(projectId)
            ?: throw NoSuchElementException("Project not found")
    }
}