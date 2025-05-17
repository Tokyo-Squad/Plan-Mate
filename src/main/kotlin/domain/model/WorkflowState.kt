package domain.model

import java.util.UUID

data class WorkflowState(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val projectId: UUID
)
