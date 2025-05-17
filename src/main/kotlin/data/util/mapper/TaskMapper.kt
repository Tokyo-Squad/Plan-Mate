package org.example.data.util.mapper

import kotlinx.datetime.LocalDateTime
import org.bson.Document
import org.example.data.remote.dto.TaskDto
import domain.model.Task
import java.util.UUID

fun TaskDto.toTaskEntity(): Task = Task(
    id = id,
    title = title,
    description = description,
    workflowStateId = stateId,
    projectId = projectId,
    createdByUserId = createdByUserId,
    createdAt = LocalDateTime.parse(createdAt)
)

fun Task.toTaskDto(): TaskDto = TaskDto(
    id = id,
    title = title,
    description = description,
    stateId = workflowStateId,
    projectId = projectId,
    createdByUserId = createdByUserId,
    createdAt = createdAt.toString()
)

fun TaskDto.toDocument(): Document = Document().apply {
    put("id", id)
    put("title", title)
    put("description", description)
    put("workflowStateId", stateId)
    put("projectId", projectId)
    put("createdByUserId", createdByUserId)
    put("createdAt", createdAt)
}

fun Document.toTaskDto(): TaskDto = TaskDto(
    id = UUID.fromString(getString("id")),
    title = getString("title"),
    description = getString("description"),
    stateId = UUID.fromString(getString("workflowStateId")),
    projectId = UUID.fromString(getString("projectId")),
    createdByUserId = UUID.fromString(getString("createdByUserId")),
    createdAt = getString("createdAt")
)