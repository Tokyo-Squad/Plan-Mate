package logic.usecase.user

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.DeleteUserUseCase
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import java.util.UUID

class DeleteUserUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var useCase: DeleteUserUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DeleteUserUseCase(repository)
    }

    @Test
    fun `should succeed when non-MATE user deletes user`() {
        // Given
        val id = UUID.randomUUID()
        val adminUser = UserEntity(
            username = "admin",
            password = "pwd",
            type = UserType.ADMIN
        )
        every { repository.delete(id) } returns Result.success(Unit)

        // When
        val result = useCase(id, adminUser)

        // Then
        assertThat(result.isSuccess)
        verify { repository.delete(id) }
    }

    @Test
    fun `should fail when MATE user deletes user`() {
        // Given
        val id = UUID.randomUUID()
        val mateUser = UserEntity(
            username = "mate",
            password = "pwd",
            type = UserType.MATE
        )

        // When
        val result = useCase(id, mateUser)

        // Then
        assertThat(result.isFailure)
        verify(exactly = 0) { repository.delete(any()) }
    }

    @Test
    fun `should fail when repository delete fails`() {
        // Given
        val id = UUID.randomUUID()
        val adminUser = UserEntity(
            username = "admin",
            password = "pwd",
            type = UserType.ADMIN
        )
        val exception = RuntimeException("Deletion error")
        every { repository.delete(id) } returns Result.failure(exception)

        // When
        val result = useCase(id, adminUser)

        // Then
        assertThat(result.isFailure)
        verify { repository.delete(id) }
    }
}
