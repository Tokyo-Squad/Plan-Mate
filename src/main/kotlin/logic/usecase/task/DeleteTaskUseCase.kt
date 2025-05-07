package org.example.logic.usecase.task

import org.example.logic.repository.TaskRepository
import java.util.UUID

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(id: UUID, currentUserId: UUID)
      = taskRepository.delete(id, currentUserId)
}