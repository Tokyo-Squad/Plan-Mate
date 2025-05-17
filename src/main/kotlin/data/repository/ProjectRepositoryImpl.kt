package org.example.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import org.example.data.RemoteDataSource
import org.example.data.remote.dto.AuditLogDto
import org.example.data.remote.dto.ProjectDto
import org.example.data.util.exception.DatabaseException
import org.example.data.util.mapper.toAuditLogDto
import domain.model.AuditAction
import domain.model.AuditLog
import domain.model.AuditedType
import domain.model.Project
import org.example.logic.repository.ProjectRepository
import toProjectDto
import toProjectEntity
import java.util.UUID

class ProjectRepositoryImpl(
    private val projectRemoteDataSource: RemoteDataSource<ProjectDto>,
    private val auditRemoteDataSource: RemoteDataSource<AuditLogDto>
) : ProjectRepository {

    override suspend fun addProject(project: Project): Project {
        require(project.name.isNotBlank()) { "Project name must not be blank" }

        projectRemoteDataSource.add(project.toProjectDto())
        logAudit(
            userId = project.createdByAdminId,
            entityId = project.id,
            action = AuditAction.CREATE,
            details = "Created project '${project.name}'"
        )
        return project
    }

    override suspend fun updateProject(project: Project, currentUserId: UUID): Project {
        projectRemoteDataSource.update(project.toProjectDto())
        logAudit(
            userId = currentUserId,
            entityId = project.id,
            action = AuditAction.UPDATE,
            details = "Updated project '${project.name}'"
        )
        return project
    }

    override suspend fun deleteProject(projectId: UUID, currentUserId: UUID) {
        val project = projectRemoteDataSource.getById(projectId)
            ?: throw DatabaseException.DatabaseItemNotFoundException(
                "Project with id '$projectId' not found"
            )
        projectRemoteDataSource.delete(projectId)
        logAudit(
            userId = currentUserId,
            entityId = projectId,
            action = AuditAction.DELETE,
            details = "Deleted project '${project.name}'"
        )
    }

    override suspend fun getAllProjects(): List<Project> =
        projectRemoteDataSource.get().map { it.toProjectEntity() }

    override suspend fun getProjectById(projectId: UUID): Project =
        projectRemoteDataSource.getById(projectId)
            ?.toProjectEntity()
            ?: throw DatabaseException.DatabaseItemNotFoundException(
                "Project with id '$projectId' not found"
            )

    private suspend fun logAudit(
        userId: UUID,
        entityId: UUID,
        action: AuditAction,
        details: String
    ) {
        val auditDto = AuditLog(
            userId = userId,
            entityType = AuditedType.PROJECT,
            entityId = entityId,
            action = action,
            changeDetails = details,
            timestamp = Clock.System.now().toLocalDateTime(UTC)
        ).toAuditLogDto()
        auditRemoteDataSource.add(auditDto)
    }
}
