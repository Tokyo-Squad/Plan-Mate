package org.example.data.mapper

import org.bson.Document
import org.example.data.remote.dto.StateDto
import org.example.entity.StateEntity
import java.util.UUID


fun StateDto.toStateEntity(): StateEntity = StateEntity(
    id = id,
    name = name,
    projectId = id
)

fun StateEntity.toStateDto(): StateDto = StateDto(
    id = id,
    name = name,
    projectId = projectId
)

fun StateDto.toDocument(): Document = Document().apply {
    put("id", id)
    put("name", name)
    put("projectId", projectId)
}

fun Document.toStateDto(): StateDto = StateDto(
    id = UUID.fromString(getString("id")),
    name = getString("name"),
    projectId = UUID.fromString(getString("projectId"))
)
