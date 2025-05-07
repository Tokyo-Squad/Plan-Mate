package org.example.logic.usecase.task

import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import java.util.UUID

class GetTaskByIdUseCase(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(id: UUID): TaskEntity = taskRepository.getTaskById(id)
}