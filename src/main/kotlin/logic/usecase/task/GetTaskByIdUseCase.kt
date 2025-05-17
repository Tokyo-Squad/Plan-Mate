package org.example.logic.usecase.task

import logic.model.Task
import org.example.logic.repository.TaskRepository
import java.util.UUID

class GetTaskByIdUseCase(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(id: UUID): Task = taskRepository.getTaskById(id)
}