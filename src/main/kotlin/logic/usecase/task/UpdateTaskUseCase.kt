package org.example.logic.usecase.task

import logic.model.Task
import org.example.logic.repository.TaskRepository
import java.util.UUID

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(task: Task, currentUserId: UUID): Task {
        if (task.title.isBlank())
            throw IllegalArgumentException("title cannot be empty")
        return taskRepository.update(task, currentUserId)
    }
}