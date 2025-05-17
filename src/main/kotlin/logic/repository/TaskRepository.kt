package org.example.logic.repository

import logic.model.Task
import java.util.UUID

interface TaskRepository {
    suspend fun add(task: Task, currentUserId: UUID)
    suspend fun delete(id: UUID, currentUserId: UUID)
    suspend fun getTaskById(id: UUID): Task
    suspend fun getTasksByProjectId(id: UUID): List<Task>
    suspend fun update(task: Task, currentUserId: UUID): Task

}