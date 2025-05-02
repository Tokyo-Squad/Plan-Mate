package org.example.data.repository


import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.data.DataProvider
import org.example.entity.AuditAction
import org.example.entity.AuditLogEntity
import org.example.entity.AuditedEntityType
import org.example.entity.TaskEntity
import org.example.logic.repository.AuditLogRepository
import org.example.logic.repository.StateRepository
import org.example.logic.repository.TaskRepository
import org.example.presentation.PlanMatException
import java.util.*

class TaskRepositoryImpl(
    private val auditLogRepository: AuditLogRepository,
    private val dataProvider: DataProvider<TaskEntity>,
    private val stateRepository: StateRepository
) : TaskRepository {

    override fun create(task: TaskEntity, currentUserId: UUID): Result<Unit> = runCatching {
        dataProvider.add(task)
        audit(
            currentUserId,
            task.id,
            AuditAction.CREATE,
            "user $currentUserId CREATE task ${task.id} at ${formatTime(now())}"
        )
    }

    override fun update(
        task: TaskEntity,
        currentUserId: UUID
    ): Result<Unit> = runCatching {
        val old = dataProvider.getById(task.id)
            ?: throw PlanMatException.ItemNotFoundException("Task ${task.id} not found")
        dataProvider.update(task)
        val details = generateUpdateDetails(old, task, currentUserId, now())
        audit(currentUserId, task.id, AuditAction.UPDATE, details)
    }

    override fun delete(id: UUID, currentUserId: UUID): Result<Unit> = runCatching {
        if (dataProvider.getById(id) == null) throw PlanMatException.ItemNotFoundException("Task $id not found")
        dataProvider.delete(id)
        audit(
            currentUserId,
            id,
            AuditAction.DELETE,
            "user $currentUserId deleted task $id at ${formatTime(now())}"
        )
    }

    override fun getTaskById(id: UUID): Result<TaskEntity> = runCatching {
        dataProvider.getById(id) ?: throw PlanMatException.ItemNotFoundException("Task $id not found")
    }

    override fun getTasksByProjectId(projectId: UUID): Result<List<TaskEntity>> = runCatching {
        dataProvider.get().filter { it.projectId == projectId }
            .takeIf { it.isNotEmpty() }
            ?: throw PlanMatException.ItemNotFoundException("Project $projectId not found")
    }

    private fun generateUpdateDetails(
        old: TaskEntity,
        new: TaskEntity,
        currentUserId: UUID,
        dateTime: LocalDateTime
    ): String {
        val base = "user $currentUserId changed task ${new.id}"
        val time = " at ${formatTime(dateTime)}"
        return when {
            new.stateId != old.stateId ->
                "$base from ${stateRepository.getById(old.stateId)} to ${stateRepository.getById(new.stateId)}$time"

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


    private fun audit(
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

