package fakeData

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.entity.TaskEntity
import java.util.UUID

fun createTaskEntityTest(
    title: String = "Title Test",
    description: String = "Desc Test",
    stateId: UUID = UUID.randomUUID(),
    projectId: UUID = UUID.randomUUID(),
    createdByUserId: UUID = UUID.randomUUID(),
    createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
): TaskEntity = TaskEntity(
    title = title,
    description = description,
    stateId = stateId,
    projectId = projectId,
    createdByUserId = createdByUserId,
    createdAt = createdAt,
)