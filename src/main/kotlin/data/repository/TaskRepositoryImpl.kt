package org.example.data.repository

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.data.RemoteDataSource
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.entity.TaskEntity
import org.example.logic.repository.AuditLogRepository
import org.example.logic.repository.StateRepository
import org.example.logic.repository.TaskRepository
import org.example.utils.PlanMateException
import java.util.UUID

class TaskRepositoryImpl(
    private val auditLogRepository: AuditLogRepository,
    private val remoteDataSource: RemoteDataSource<TaskEntity>,
    private val stateRepository: StateRepository
) : TaskRepository {

    override suspend fun add(task: TaskEntity, currentUserId: UUID) {
        remoteDataSource.add(task)
        audit(
            currentUserId,
            task.id,
            AuditAction.CREATE,
            "user $currentUserId CREATE task ${task.id} at ${formatTime(now())}"
        )
    }

    override suspend fun update(
        task: TaskEntity,
        currentUserId: UUID
    ) {
        val old = remoteDataSource.getById(task.id)
            ?: throw PlanMateException.ItemNotFoundException("Task ${task.id} not found")
        remoteDataSource.update(task)
        val details = generateUpdateDetails(old, task, currentUserId, now())
        audit(currentUserId, task.id, AuditAction.UPDATE, details)
    }

    override suspend fun delete(id: UUID, currentUserId: UUID) {
        if (remoteDataSource.getById(id) == null) throw PlanMateException.ItemNotFoundException("Task $id not found")
        remoteDataSource.delete(id)
        audit(
            currentUserId,
            id,
            AuditAction.DELETE,
            "user $currentUserId deleted task $id at ${formatTime(now())}"
        )
    }

    override suspend fun getTaskById(id: UUID): TaskEntity =
        remoteDataSource.getById(id) ?: throw PlanMateException.ItemNotFoundException("Task $id not found")


    override suspend fun getTasksByProjectId(id: UUID): List<TaskEntity> =
        remoteDataSource.get().filter { it.projectId == id }
            .takeIf { it.isNotEmpty() }
            ?: throw PlanMateException.ItemNotFoundException("Project $id not found")


    private suspend fun generateUpdateDetails(
        old: TaskEntity,
        new: TaskEntity,
        currentUserId: UUID,
        dateTime: LocalDateTime
    ): String {
        val base = "user $currentUserId changed task ${new.id}"
        val time = " at ${formatTime(dateTime)}"
        return when {
            new.stateId != old.stateId ->
                "$base from ${stateRepository.getStateById(old.stateId)} to ${stateRepository.getStateById(new.stateId)}$time"

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
        AuditLogEntity(
            userId = userId, entityType = AuditedEntityType.TASK,
            entityId = entityId,
            timestamp = now(),
            action = action,
            changeDetails = details
        )
    )
}