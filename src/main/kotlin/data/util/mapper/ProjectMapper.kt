import kotlinx.datetime.LocalDateTime
import org.bson.Document
import org.example.data.remote.dto.ProjectDto
import domain.model.Project
import java.util.UUID

fun ProjectDto.toProjectEntity(): Project = Project(
    id = id,
    name = name,
    createdByAdminId = UUID.fromString(createdByAdminId),
    createdAt = LocalDateTime.parse(createdAt)
)

fun Project.toProjectDto(): ProjectDto = ProjectDto(
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