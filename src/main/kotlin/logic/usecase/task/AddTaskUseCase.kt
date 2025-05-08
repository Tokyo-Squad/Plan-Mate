package org.example.logic.usecase.task

import org.example.entity.TaskEntity
import org.example.logic.repository.TaskRepository
import org.example.utils.PlanMateException
import java.util.UUID

class AddTaskUseCase(
    private val taskRepository: TaskRepository,
) {
     suspend operator fun invoke(task: TaskEntity, currentUserId: UUID) {
        if (task.title.isBlank()) throw PlanMateException.ValidationException("title cannot be empty")
        taskRepository.add(task, currentUserId)
    }
}