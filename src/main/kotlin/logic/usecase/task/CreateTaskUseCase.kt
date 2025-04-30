package org.example.logic.usecase.task

import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import java.util.UUID

class CreateTaskUseCase (
    private val taskRepository: TaskRepository,
){
    operator fun invoke(task: TaskEntity,currentUserId: UUID): Result<Unit> {
        return Result.failure(Exception("CreateTaskUseCase"))
    }
}