package org.example.logic.usecase.task

import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import java.util.UUID

class GetTasksByProjectIdUseCase(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(projectId: UUID):List<TaskEntity> = taskRepository.getTasksByProjectId(projectId)
}