package fakeData

import org.example.entity.StateEntity
import java.util.UUID

class StateFakeData {
    fun createState(
        id: UUID = UUID.randomUUID(),
        name: String = "To Do",
        projectId: UUID = UUID.randomUUID()
    ): StateEntity {
        return StateEntity(
            id = id,
            name = name,
            projectId = projectId
        )
    }
}