package org.example.logic.usecase.task

import logic.model.Task
import org.example.logic.repository.TaskRepository
import java.util.UUID

class GetTasksByProjectIdUseCase(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(projectId: UUID):List<Task> = taskRepository.getTasksByProjectId(projectId)
}