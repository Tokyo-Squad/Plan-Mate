import kotlinx.datetime.LocalDateTime
import org.bson.Document
import org.example.data.remote.dto.ProjectDto
import org.example.entity.ProjectEntity
import java.util.UUID

fun ProjectDto.toProjectEntity(): ProjectEntity = ProjectEntity(
    id = id,
    name = name,
    createdByAdminId = UUID.fromString(createdByAdminId),
    createdAt = LocalDateTime.parse(createdAt)
)

fun ProjectEntity.toProjectDto(): ProjectDto = ProjectDto(
    id = id,
    name = name,
    createdByAdminId = createdByAdminId.toString(),
    createdAt = createdAt.toString()
)

fun ProjectDto.toDocument(): Document = Document().apply {
    put("id", id)
    put("name", name)
    put("createdByAdminId", createdByAdminId)
    put("createdAt", createdAt)
}

fun Document.toProjectDto(): ProjectDto = ProjectDto(
    id = UUID.fromString(getString("id")),
    name = getString("name"),
    createdByAdminId = getString("createdByAdminId"),
    createdAt = getString("createdAt")
)