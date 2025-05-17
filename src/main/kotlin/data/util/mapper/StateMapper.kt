package org.example.data.util.mapper

import org.bson.Document
import org.example.data.remote.dto.WorkflowStateDto
import domain.model.WorkflowState
import java.util.UUID


fun WorkflowStateDto.toStateEntity(): WorkflowState = WorkflowState(
    id = id,
    name = name,
    projectId = id
)

fun WorkflowState.toStateDto(): WorkflowStateDto = WorkflowStateDto(
    id = id,
    name = name,
    projectId = projectId
)

fun WorkflowStateDto.toDocument(): Document = Document().apply {
    put("id", id)
    put("name", name)
    put("projectId", projectId)
}

fun Document.toStateDto(): WorkflowStateDto = WorkflowStateDto(
    id = UUID.fromString(getString("id")),
    name = getString("name"),
    projectId = UUID.fromString(getString("projectId"))
)
