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
import org.example.utils.PlanMatException
import java.util.*

class ProjectRepositoryImpl(
    private val projectDataProvider: DataProvider<ProjectEntity>,
    private val auditDataProvider: DataProvider<AuditLogEntity>
) : ProjectRepository {

    override fun addProject(project: ProjectEntity): Result<ProjectEntity> {
        return try {
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
            Result.success(project)
        } catch (e: PlanMatException) {
            Result.failure(e)
        }
    }

    override fun updateProject(project: ProjectEntity, currentUserId: UUID): Result<ProjectEntity> {
        return try {
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
            Result.success(project)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun deleteProject(projectId: UUID, currentUserId: UUID): Result<Unit> {
        return try {
            val project = projectDataProvider.getById(projectId) ?: throw NoSuchElementException("Project not found")

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
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllProjects(): Result<List<ProjectEntity>> {
        return try {
            Result.success(projectDataProvider.get())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getProjectById(projectId: String): Result<ProjectEntity> {
        return try {
            val uuid = UUID.fromString(projectId)
            val project = projectDataProvider.getById(uuid) ?: throw NoSuchElementException("Project not found")
            Result.success(project)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}