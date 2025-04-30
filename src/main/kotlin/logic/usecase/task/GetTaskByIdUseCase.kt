package org.example.logic.usecase.task

import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import java.util.UUID

class GetTaskByIdUseCase(
    private val taskRepository: TaskRepository,
) {

    operator fun invoke(id: UUID): Result<TaskEntity> {
        return Result.failure(Exception("GetTaskByIdUseCase"))
    }
}