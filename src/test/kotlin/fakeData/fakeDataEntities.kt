package fakeData

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import org.example.entity.ProjectEntity
import org.example.entity.UserEntity
import org.example.entity.UserType
import java.util.*

fun fakeAdminEntity(): UserEntity {
    return UserEntity(
        id = UUID.randomUUID(),
        username = "test_user",
        password = "hashed_password_123",
        type = UserType.ADMIN
    )
}

fun fakeRegularUserEntity(): UserEntity {
    return UserEntity(
        id = UUID.randomUUID(),
        username = "test_user",
        password = "hashed_password_123",
        type = UserType.MATE
    )
}

fun fakeProjectEntity(): ProjectEntity {
    return ProjectEntity(
        id = UUID.randomUUID(),
        name = "Test Project",
        createdByAdminId = fakeAdminEntity().id,
        createdAt = Clock.System.now().toLocalDateTime(UTC)
    )
}
