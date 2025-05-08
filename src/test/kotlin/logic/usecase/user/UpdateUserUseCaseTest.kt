package logic.usecase.user

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.UpdateUserUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.Test

class UpdateUserUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var updateUseCase: UpdateUserUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        updateUseCase = UpdateUserUseCase(repository)
    }

    @Test
    fun `should succeed when ADMIN user updates user`() = runTest {
        // Given
        val userToUpdate = UserEntity(
            username = "user1",
            password = "pwd1",
            type = UserType.MATE
        )
        val adminUser = UserEntity(
            username = "admin",
            password = "pwd2",
            type = UserType.ADMIN
        )
        coEvery { repository.update(userToUpdate) } returns Unit

        // When/Then
        assertDoesNotThrow {
            updateUseCase(userToUpdate, adminUser)
        }

        coVerify { repository.update(userToUpdate) }
    }

    @Test
    fun `should throw exception when MATE user updates user`() = runTest {
        // Given
        val userToUpdate = UserEntity(
            username = "user2",
            password = "pwd",
            type = UserType.ADMIN
        )
        val mateUser = UserEntity(
            username = "mate",
            password = "pwd2",
            type = UserType.MATE
        )

        // When/Then
        val exception = assertThrows<PlanMateException.UserActionNotAllowedException> {
            updateUseCase(userToUpdate, mateUser)
        }

        assert(exception.message?.contains("MATE users are not allowed") == true)
        coVerify(exactly = 0) { repository.update(any()) }
    }

    @Test
    fun `should throw exception when repository update fails`() = runTest {
        // Given
        val userToUpdate = UserEntity(
            username = "user3",
            password = "pwd3",
            type = UserType.MATE
        )
        val adminUser = UserEntity(
            username = "admin",
            password = "pwd4",
            type = UserType.ADMIN
        )
        val exception = RuntimeException("Update error")

        coEvery { repository.update(userToUpdate) } throws exception

        // When/Then
        val thrown = assertThrows<RuntimeException> {
            updateUseCase(userToUpdate, adminUser)
        }

        assertEquals("Update error", thrown.message)
        coVerify { repository.update(userToUpdate) }
    }

    @Test
    fun `should throw exception when updating non-existent user`() = runTest {
        // Given
        val userToUpdate = UserEntity(
            username = "nonexistent",
            password = "pwd",
            type = UserType.MATE
        )
        val adminUser = UserEntity(
            username = "admin",
            password = "pwd",
            type = UserType.ADMIN
        )

        coEvery { repository.update(userToUpdate) } throws
                PlanMateException.ItemNotFoundException("User not found")

        // When/Then
        val exception = assertThrows<PlanMateException.ItemNotFoundException> {
            updateUseCase(userToUpdate, adminUser)
        }

        assert(exception.message?.contains("User not found") == true)
        coVerify { repository.update(userToUpdate) }
    }

    @Test
    fun `should validate user data before update`() = runTest {
        // Given
        val invalidUser = UserEntity(
            id = UUID.randomUUID(),
            username = "",
            password = "pwd",
            type = UserType.MATE
        )
        val adminUser = UserEntity(
            id = UUID.randomUUID(),
            username = "admin",
            password = "pwd",
            type = UserType.ADMIN
        )

        // When/Then
        val exception = assertThrows<PlanMateException.ValidationException> {
            updateUseCase(invalidUser, adminUser)
        }

        assertThat(exception.message).contains("Username cannot be empty")
        coVerify(exactly = 0) { repository.update(any()) }
    }
}
