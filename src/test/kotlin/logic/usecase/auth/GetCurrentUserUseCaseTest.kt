package logic.usecase.auth

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.data.util.exception.AuthenticationException
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFailsWith

class GetCurrentUserUseCaseTest {
    private val authRepository = mockk<AuthenticationRepository>()
    private val getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)

    @Test
    fun `should return user when user is authenticated`() = runTest {
        // arrange
        val currentUser = UserEntity(
            username = "testUser",
            password = "password123",
            type = UserType.MATE
        )
        coEvery { authRepository.getCurrentUser() } returns currentUser

        // act
        val result = getCurrentUserUseCase()

        // assert
        assertEquals(currentUser, result)
        coVerify { authRepository.getCurrentUser() }
    }

    @Test
    fun `should throw exception when no user is authenticated`() = runTest {
        // arrange
        coEvery { authRepository.getCurrentUser() } throws AuthenticationException.NoCurrentUser()

        // assert
        assertFailsWith<AuthenticationException.NoCurrentUser>{
            getCurrentUserUseCase()
        }
        coVerify { authRepository.getCurrentUser() }
    }

    @Test
    fun `should throw exception when repository operation fails`() = runTest {
        // arrange
        val expectedError = RuntimeException("Failed to get current user")
        coEvery { authRepository.getCurrentUser() } throws expectedError

        // act/assert
        val thrown = assertThrows<RuntimeException> {
            getCurrentUserUseCase()
        }

        assertEquals("Failed to get current user", thrown.message)
        coVerify { authRepository.getCurrentUser() }
    }
}