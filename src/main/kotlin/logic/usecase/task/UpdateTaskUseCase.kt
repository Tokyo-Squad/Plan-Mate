package org.example.logic.usecase.task

import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import java.util.UUID

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(
        task: TaskEntity,
        currentUserId: UUID
    ): Result<Unit> {
        if (task.title.isBlank()) {
            return Result.failure(IllegalArgumentException("title cannot be empty"))
        }
        return runCatching { taskRepository.update(task,currentUserId).getOrThrow() }
    }
}