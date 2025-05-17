package logic.usecase.user

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.entity.User
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.GetUsersUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class GetUsersUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUsersUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = GetUsersUseCase(repository)
    }

    @Test
    fun `should return empty list when repository returns empty`() = runTest {
        // Given
        val emptyList = emptyList<User>()
        coEvery { repository.getUsers() } returns emptyList

        // When
        val result = useCase()

        // Then
        assertTrue(result.isEmpty())
        coVerify { repository.getUsers() }
    }

    @Test
    fun `should return list of users when repository returns data`() = runTest {
        // Given
        val users = listOf(
            User(
                username = "username1",
                password = "password1",
                type = UserType.MATE
            ),
            User(
                username = "username2",
                password = "password2",
                type = UserType.MATE
            )
        )
        coEvery { repository.getUsers() } returns users

        // When
        val result = useCase()

        // Then
        assertEquals(users, result)
        assertEquals(2, result.size)
        coVerify { repository.getUsers() }
    }

    @Test
    fun `should throw exception when repository operation fails`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { repository.getUsers() } throws exception

        // When/Then
        val thrown = assertThrows<RuntimeException> {
            useCase()
        }

        assertEquals("Database error", thrown.message)
        coVerify { repository.getUsers() }
    }

    @Test
    fun `should handle empty result properly`() = runTest {
        // Given
        coEvery { repository.getUsers() } returns emptyList()

        // When
        val result = useCase()

        // Then
        assertTrue(result.isEmpty())
        coVerify { repository.getUsers() }
    }
}