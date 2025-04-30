package org.example.logic.usecase.task

import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import java.util.UUID

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
) {

    operator fun invoke(taskEntity: TaskEntity, currentUserId: UUID): Result<TaskEntity> {
        return Result.failure(Exception("UpdateTaskUseCase"))
    }
}