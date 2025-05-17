package org.example.logic.usecase.task

import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import java.util.UUID

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(task: TaskEntity, currentUserId: UUID): TaskEntity {
        if (task.title.isBlank())
            throw IllegalArgumentException("title cannot be empty")
        return taskRepository.update(task, currentUserId)
    }
}