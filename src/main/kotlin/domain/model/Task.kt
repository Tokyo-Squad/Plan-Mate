package domain.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Task(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val workflowStateId: UUID,
    val projectId: UUID,
    val createdByUserId: UUID,
    val createdAt: LocalDateTime
)