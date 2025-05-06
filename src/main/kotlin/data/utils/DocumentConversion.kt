package org.example.data.utils

import kotlinx.datetime.LocalDateTime
import org.bson.Document
import org.example.entity.ProjectEntity
import java.util.*

fun ProjectEntity.toDocument(): Document {
    return Document().apply {
        put("_id", id)
        put("name", name)
        put("createdByAdminId", createdByAdminId)
        put("createdAt", createdAt.toString())
    }
}

fun Document.toProjectEntity(): ProjectEntity {
    return ProjectEntity(
        id = UUID.fromString("_id"),
        name = getString("name"),
        createdByAdminId = UUID.fromString("createdByAdminId"),
        createdAt = LocalDateTime.parse(getString("createdAt"))
    )
}