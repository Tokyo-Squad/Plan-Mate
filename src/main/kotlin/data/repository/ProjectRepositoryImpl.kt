package org.example.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import org.example.data.DataProvider
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
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
        auditDataProvider.add(
            AuditLogEntity(
                userId = project.createdByAdminId,
                entityType = AuditedEntityType.PROJECT,
                entityId = project.id,
                action = AuditAction.CREATE,
                changeDetails = "Created project: ${project.name}",
                timestamp = Clock.System.now().toLocalDateTime(UTC)
            )
        )
        return project
    }

    override suspend fun updateProject(project: ProjectEntity, currentUserId: UUID): ProjectEntity {
        projectDataProvider.update(project)

        auditDataProvider.add(
            AuditLogEntity(
                userId = currentUserId,
                entityType = AuditedEntityType.PROJECT,
                entityId = project.id,
                action = AuditAction.UPDATE,
                changeDetails = "Updated project: ${project.name}",
                timestamp = Clock.System.now().toLocalDateTime(UTC)
            )
        )
        return project
    }

    override suspend fun deleteProject(projectId: UUID, currentUserId: UUID) {
        val project = projectDataProvider.getById(projectId)
            ?: throw NoSuchElementException("Project not found")
        projectDataProvider.delete(projectId)

        auditDataProvider.add(
            AuditLogEntity(
                userId = currentUserId,
                entityType = AuditedEntityType.PROJECT,
                entityId = projectId,
                action = AuditAction.DELETE,
                changeDetails = "Deleted project: ${project.name}",
                timestamp = Clock.System.now().toLocalDateTime(UTC)
            )
        )
    }

    override suspend fun getAllProjects(): List<ProjectEntity> = projectDataProvider.get()

    override suspend fun getProjectById(projectId: UUID): ProjectEntity {
        return projectDataProvider.getById(projectId)
            ?: throw NoSuchElementException("Project not found")
    }
}