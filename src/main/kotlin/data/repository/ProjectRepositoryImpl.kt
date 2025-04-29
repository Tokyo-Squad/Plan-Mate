package org.example.data.repository

import kotlinx.datetime.toLocalDateTime
import org.example.data.utils.ProjectDetailsIndex
import org.example.entity.ProjectEntity
import org.example.logic.repository.ProjectRepository
import java.util.UUID

class ProjectRepositoryImpl : ProjectRepository {

    override fun createProject(project: ProjectEntity, currentUser: String): Result<ProjectEntity> {
        TODO("Not yet implemented")
    }

    override fun updateProject(project: ProjectEntity, currentUser: String): Result<ProjectEntity> {
        TODO("Not yet implemented")
    }

    override fun deleteProject(projectId: UUID, currentUser: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun getAllProjects(): Result<List<ProjectEntity>> {
        TODO("Not yet implemented")
    }

    override fun getProjectById(projectId: String): Result<ProjectEntity> {
        TODO("Not yet implemented")
    }

    private fun convertProjectToCsv(project: ProjectEntity): String {
        return listOf(
            project.id,
            project.name,
            project.description,
            project.createdByAdminId,
            project.createdAt.toString(),
        ).joinToString(",")
    }

    private fun convertCsvToProject(line: String): ProjectEntity {
        val projectDetails = line.split(",")
        require(projectDetails.size == 5) { "Invalid CSV format it expected 5 columns, check the csv file" }

        return ProjectEntity(
            id = UUID.fromString(projectDetails[ProjectDetailsIndex.ID_INDEX]),
            name = projectDetails[ProjectDetailsIndex.NAME_INDEX],
            description = projectDetails[ProjectDetailsIndex.DESCRIPTION_INDEX],
            createdByAdminId = UUID.fromString(projectDetails[ProjectDetailsIndex.CREATED_BY_INDEX]),
            createdAt = projectDetails[ProjectDetailsIndex.CREATED_AT_INDEX].toLocalDateTime(),
        )
    }
}