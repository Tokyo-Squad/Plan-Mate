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
import org.example.utils.PlanMateException
import java.util.*

class ProjectRepositoryImpl(
    private val projectDataProvider: DataProvider<ProjectEntity>,
    private val auditDataProvider: DataProvider<AuditLogEntity>
) : ProjectRepository {

    override suspend fun addProject(project: ProjectEntity): ProjectEntity {
        require(project.name.isNotBlank()) { "Project name cannot be blank" }

        try {
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
        } catch (e: Exception) {
            throw when (e) {
                is PlanMateException -> e
                else -> PlanMateException.DatabaseException("Failed to add project: ${e.message}")
            }
        }
    }

    override suspend fun updateProject(project: ProjectEntity, currentUserId: UUID): ProjectEntity {
        try {
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
        } catch (e: Exception) {
            throw when (e) {
                is PlanMateException -> e
                is NoSuchElementException -> e
                else -> PlanMateException.DatabaseException("Failed to update project: ${e.message}")
            }
        }
    }

    override suspend fun deleteProject(projectId: UUID, currentUserId: UUID) {
        val project = projectDataProvider.getById(projectId)
            ?: throw NoSuchElementException("Project not found")

        try {
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
        } catch (e: Exception) {
            throw when (e) {
                is PlanMateException -> e
                else -> PlanMateException.DatabaseException("Failed to delete project: ${e.message}")
            }
        }
    }

    override suspend fun getAllProjects(): List<ProjectEntity> {
        return try {
            projectDataProvider.get()
        } catch (e: Exception) {
            throw PlanMateException.DatabaseException("Failed to get projects: ${e.message}")
        }
    }

    override suspend fun getProjectById(projectId: String): ProjectEntity {
        val uuid = try {
            UUID.fromString(projectId)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid project ID format")
        }

        return projectDataProvider.getById(uuid)
            ?: throw NoSuchElementException("Project not found")
    }
}