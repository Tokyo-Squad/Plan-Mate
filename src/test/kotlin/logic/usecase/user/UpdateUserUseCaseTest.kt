package logic.usecase.user

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.UpdateUserUseCase
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class UpdateUserUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var updateUseCase: UpdateUserUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        updateUseCase = UpdateUserUseCase(repository)
    }

    @Test
    fun `should succeed when ADMIN user updates user`() {
        // Given
        val userToUpdate = UserEntity(username = "user1", password = "pwd1", type = UserType.MATE)
        val adminUser = UserEntity(username = "admin", password = "pwd2", type = UserType.ADMIN)
        every { repository.update(userToUpdate) } returns Result.success(Unit)

        // When
        val result = updateUseCase(userToUpdate, adminUser)

        // Then
        assertThat(result.isSuccess)
        verify { repository.update(userToUpdate) }
    }

    @Test
    fun `should fail when MATE user updates user`() {
        // Given
        val userToUpdate = UserEntity(username = "user2", password = "pwd", type = UserType.ADMIN)
        val mateUser = UserEntity(username = "mate", password = "pwd2", type = UserType.MATE)

        // When
        val result = updateUseCase(userToUpdate, mateUser)

        // Then
        assertThat(result.isFailure)
        verify(exactly = 0) { repository.update(any()) }
    }

    @Test
    fun `should fail when repository update fails`() {
        // Given
        val userToUpdate = UserEntity(username = "user3", password = "pwd3", type = UserType.MATE)
        val adminUser = UserEntity(username = "admin", password = "pwd4", type = UserType.ADMIN)
        val exception = RuntimeException("Update error")

        every { repository.update(userToUpdate) } returns Result.failure(exception)

        // When
        val result = updateUseCase(userToUpdate, adminUser)

        // Then
        assertThat(result.isFailure)
        verify { repository.update(userToUpdate) }
    }
}

