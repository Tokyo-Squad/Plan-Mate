package org.example.logic.usecase.task

import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import java.util.UUID

class GetTasksByProjectIdUseCase(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(projectId: UUID): Result<List<TaskEntity>> =
        runCatching { taskRepository.getTasksByProjectId(projectId).getOrThrow() }
}