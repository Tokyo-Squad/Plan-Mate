package org.example.logic.repository

import org.example.entity.TaskEntity
import java.util.UUID

interface TaskRepository {
    suspend fun add(task: TaskEntity, currentUserId: UUID)
    suspend fun delete(id: UUID, currentUserId: UUID)
    suspend fun getTaskById(id: UUID): TaskEntity
    suspend fun getTasksByProjectId(id: UUID): List<TaskEntity>
    suspend fun update(task: TaskEntity, currentUserId: UUID): TaskEntity

}