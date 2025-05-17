package org.example.logic.usecase.task

import domain.model.Task
import org.example.logic.repository.TaskRepository
import domain.utils.exception.PlanMateException
import java.util.UUID

class AddTaskUseCase(
    private val taskRepository: TaskRepository,
) {
     suspend operator fun invoke(task: Task, currentUserId: UUID) {
        if (task.title.isBlank()) throw PlanMateException.ValidationException("title cannot be empty")
        taskRepository.add(task, currentUserId)
    }
}