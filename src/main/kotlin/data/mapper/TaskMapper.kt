package org.example.data.mapper

import kotlinx.datetime.LocalDateTime
import org.bson.Document
import org.example.data.remote.dto.TaskDto
import org.example.entity.TaskEntity
import java.util.UUID

fun TaskDto.toTaskEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    stateId = stateId,
    projectId = projectId,
    createdByUserId = createdByUserId,
    createdAt = LocalDateTime.parse(createdAt)
)

fun TaskEntity.toTaskDto(): TaskDto = TaskDto(
    id = id,
    title = title,
    description = description,
    stateId = stateId,
    projectId = projectId,
    createdByUserId = createdByUserId,
    createdAt = createdAt.toString()
)

fun TaskDto.toDocument(): Document = Document().apply {
    put("id", id)
    put("title", title)
    put("description", description)
    put("stateId", stateId)
    put("projectId", projectId)
    put("createdByUserId", createdByUserId)
    put("createdAt", createdAt)
}

fun Document.toTaskDto(): TaskDto = TaskDto(
    id = UUID.fromString(getString("id")),
    title = getString("title"),
    description = getString("description"),
    stateId = UUID.fromString(getString("stateId")),
    projectId = UUID.fromString(getString("projectId")),
    createdByUserId = UUID.fromString(getString("createdByUserId")),
    createdAt = getString("createdAt")
)