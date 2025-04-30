package org.example.data.repository

import org.example.data.DataProvider
import org.example.entity.TaskEntity
import org.example.logic.repository.AuditLogRepository
import org.example.logic.repository.TaskRepository
import java.util.UUID

class TaskRepositoryImpl(
    private val auditLogRepository: AuditLogRepository,
    private val dataProvider: DataProvider<TaskEntity>
) : TaskRepository {
    override fun create(task: TaskEntity, currentUserId: UUID): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun update(task: TaskEntity, currentUserId: UUID): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun delete(id: UUID, currentUserId: UUID): Result<Unit> {
        TODO("Not yet implemented")
    }


    override fun getTaskById(id: UUID): Result<TaskEntity> {
        TODO("Not yet implemented")
    }

    override fun getTasksByProjectId(id: UUID): Result<List<TaskEntity>> {
        TODO("Not yet implemented")
    }


}