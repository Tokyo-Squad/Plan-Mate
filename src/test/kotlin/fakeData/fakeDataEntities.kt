package fakeData

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import domain.model.Project
import org.example.entity.User
import org.example.entity.UserType
import java.util.*

fun fakeAdminEntity(): User {
    return User(
        id = UUID.randomUUID(),
        username = "test_user",
        password = "hashed_password_123",
        type = UserType.ADMIN
    )
}

fun fakeRegularUserEntity(): User {
    return User(
        id = UUID.randomUUID(),
        username = "test_user",
        password = "hashed_password_123",
        type = UserType.MATE
    )
}

fun fakeProjectEntity(): Project {
    return Project(
        id = UUID.randomUUID(),
        name = "Test Project",
        createdByAdminId = fakeAdminEntity().id,
        createdAt = Clock.System.now().toLocalDateTime(UTC)
    )
}
