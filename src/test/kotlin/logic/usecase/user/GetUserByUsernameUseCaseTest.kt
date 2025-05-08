package logic.usecase.user

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.GetUserByUsernameUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class GetUserByUsernameUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUserByUsernameUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = GetUserByUsernameUseCase(repository)
    }

    @Test
    fun `should return UserEntity when repository finds by username`() = runTest {
        // Given
        val expected = UserEntity(
            username = "john",
            password = "john123",
            type = UserType.MATE
        )
        coEvery { repository.getUserByUsername("john") } returns expected

        // When
        val result = useCase("john")

        // Then
        assertEquals(expected, result)
        coVerify { repository.getUserByUsername("john") }
    }

    @Test
    fun `should throw ValidationException when username is empty`() = runTest {
        // Given
        val emptyUsername = ""

        // When/Then
        val exception = assertThrows<PlanMateException.ValidationException> {
            useCase(emptyUsername)
        }

        assertThat(exception.message).contains("Username cannot be empty")
        coVerify(exactly = 0) { repository.getUserByUsername(any()) }
    }

    @Test
    fun `should throw ItemNotFoundException when user not found`() = runTest {
        // Given
        val username = "nonexistent"
        coEvery { repository.getUserByUsername(username) } throws
                PlanMateException.ItemNotFoundException("User not found with username: $username")

        // When/Then
        val exception = assertThrows<PlanMateException.ItemNotFoundException> {
            useCase(username)
        }

        assert(exception.message?.contains("User not found") == true)
        coVerify { repository.getUserByUsername(username) }
    }

    @Test
    fun `should throw exception when repository operation fails`() = runTest {
        // Given
        val username = "john"
        val exception = RuntimeException("Database error")
        coEvery { repository.getUserByUsername(username) } throws exception

        // When/Then
        val thrown = assertThrows<RuntimeException> {
            useCase(username)
        }

        assertEquals("Database error", thrown.message)
        coVerify { repository.getUserByUsername(username) }
    }
}