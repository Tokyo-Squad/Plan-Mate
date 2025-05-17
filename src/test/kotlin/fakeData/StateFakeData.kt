package fakeData

import domain.model.WorkflowState
import java.util.UUID

class StateFakeData {
    fun createState(
        id: UUID = UUID.randomUUID(),
        name: String = "To Do",
        projectId: UUID = UUID.randomUUID()
    ): WorkflowState {
        return WorkflowState(
            id = id,
            name = name,
            projectId = projectId
        )
    }
}