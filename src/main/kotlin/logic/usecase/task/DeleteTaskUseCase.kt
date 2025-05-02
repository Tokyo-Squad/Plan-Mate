package org.example.logic.usecase.task

import org.example.logic.repository.TaskRepository
import java.util.UUID

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(id: UUID, currentUserId: UUID): Result<Unit> =
        runCatching { taskRepository.delete(id, currentUserId).getOrThrow() }
}