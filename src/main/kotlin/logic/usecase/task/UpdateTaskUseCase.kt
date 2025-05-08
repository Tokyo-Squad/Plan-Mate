package org.example.logic.usecase.task

import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import java.util.*

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(task: TaskEntity, currentUserId: UUID) {
        if (task.title.isBlank())
            throw IllegalArgumentException("title cannot be empty")
        taskRepository.update(task, currentUserId)
    }
}