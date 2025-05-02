package org.example.logic.repository

import org.example.entity.AuditedEntityType
import org.example.entity.TaskEntity
import java.util.UUID

interface TaskRepository {
    fun create(task: TaskEntity,currentUserId:UUID): Result<Unit>
    fun delete(id: UUID,currentUserId:UUID): Result<Unit>
    fun getTaskById(id: UUID): Result<TaskEntity>
    fun getTasksByProjectId(id: UUID): Result<List<TaskEntity>>
    fun update(task: TaskEntity, currentUserId: UUID): Result<Unit>

}