package logic.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Project(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val createdByAdminId: UUID,
    val createdAt: LocalDateTime,
)
