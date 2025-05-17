package org.example.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import org.example.data.RemoteDataSource
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.entity.ProjectEntity
import org.example.logic.repository.ProjectRepository
import java.util.*

class ProjectRepositoryImpl(
    private val projectRemoteDataSource: RemoteDataSource<ProjectEntity>,
    private val auditRemoteDataSource: RemoteDataSource<AuditLogEntity>
) : ProjectRepository {

    override suspend fun addProject(project: ProjectEntity): ProjectEntity {
        require(project.name.isNotBlank()) { "Project name cannot be blank" }

        projectRemoteDataSource.add(project)
        auditRemoteDataSource.add(
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
        projectRemoteDataSource.update(project)

        auditRemoteDataSource.add(
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
        val project = projectRemoteDataSource.getById(projectId)
            ?: throw NoSuchElementException("Project not found")
        projectRemoteDataSource.delete(projectId)

        auditRemoteDataSource.add(
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

    override suspend fun getAllProjects(): List<ProjectEntity> = projectRemoteDataSource.get()

    override suspend fun getProjectById(projectId: UUID): ProjectEntity {
        return projectRemoteDataSource.getById(projectId)
            ?: throw NoSuchElementException("Project not found")
    }
}