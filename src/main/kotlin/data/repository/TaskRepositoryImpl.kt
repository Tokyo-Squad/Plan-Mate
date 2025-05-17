package org.example.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.data.RemoteDataSource
import org.example.data.remote.dto.TaskDto
import org.example.data.util.exception.DatabaseException
import org.example.data.util.mapper.toTaskDto
import org.example.data.util.mapper.toTaskEntity
import domain.model.AuditAction
import domain.model.AuditLog
import domain.model.AuditedType
import domain.model.Task
import org.example.logic.repository.AuditLogRepository
import org.example.logic.repository.WorkflowStateRepository
import org.example.logic.repository.TaskRepository
import java.util.UUID

class TaskRepositoryImpl(
    private val auditLogRepository: AuditLogRepository,
    private val remoteDataSource: RemoteDataSource<TaskDto>,
    private val workflowStateRepository: WorkflowStateRepository
) : TaskRepository {

    override suspend fun add(task: Task, currentUserId: UUID) {
        remoteDataSource.add(task.toTaskDto())
        audit(
            currentUserId,
            task.id,
            AuditAction.CREATE,
            "user $currentUserId CREATE task ${task.id} at ${formatTime(now())}"
        )
    }

    override suspend fun update(
        task: Task,
        currentUserId: UUID
    ): Task {
        val old = remoteDataSource.getById(task.id)?.toTaskEntity()
            ?: throw DatabaseException.DatabaseItemNotFoundException("Task ${task.id} not found")
        remoteDataSource.update(task.toTaskDto())
        val details = generateUpdateDetails(old, task, currentUserId, now())
        audit(currentUserId, task.id, AuditAction.UPDATE, details)
        return task
    }

    override suspend fun delete(id: UUID, currentUserId: UUID) {
        if (remoteDataSource.getById(id) == null) throw DatabaseException.DatabaseItemNotFoundException("Task $id not found")
        remoteDataSource.delete(id)
        audit(
            currentUserId,
            id,
            AuditAction.DELETE,
            "user $currentUserId deleted task $id at ${formatTime(now())}"
        )
    }

    override suspend fun getTaskById(id: UUID): Task =
        remoteDataSource.getById(id)?.toTaskEntity()
            ?: throw DatabaseException.DatabaseItemNotFoundException("Task $id not found")


    override suspend fun getTasksByProjectId(id: UUID): List<Task> =
        remoteDataSource.get().filter { it.projectId == id }
            .takeIf { it.isNotEmpty() }?.map { it.toTaskEntity() }
            ?: throw DatabaseException.DatabaseItemNotFoundException("Project $id not found")


    private suspend fun generateUpdateDetails(
        old: Task,
        new: Task,
        currentUserId: UUID,
        dateTime: LocalDateTime
    ): String {
        val base = "user $currentUserId changed task ${new.id}"
        val time = " at ${formatTime(dateTime)}"
        return when {
            new.workflowStateId != old.workflowStateId ->
                "$base from ${workflowStateRepository.getStateById(old.workflowStateId)} to ${workflowStateRepository.getStateById(new.workflowStateId)}$time"

            new.title != old.title ->
                "$base renamed from '${old.title}' to '${new.title}'$time"

            new.description != old.description ->
                "$base updated description$time"

            else ->
                "$base$time"
        }
    }


    private fun now() = Clock.System.now().toLocalDateTime(TimeZone.UTC)
    private fun formatTime(dateTime: LocalDateTime): String {
        val hour = dateTime.hour
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        val amPm = if (hour < 12) "AM" else "PM"
        return "%04d/%02d/%02d %d:%02d %s".format(
            dateTime.year, dateTime.monthNumber, dateTime.dayOfMonth, hour12,
            dateTime.minute,
            amPm
        )
    }

    private suspend fun audit(
        userId: UUID,
        entityId: UUID,
        action: AuditAction,
        details: String
    ) = auditLogRepository.addAudit(
        AuditLog(
            userId = userId, entityType = AuditedType.TASK,
            entityId = entityId,
            timestamp = now(),
            action = action,
            changeDetails = details
        )
    )
}