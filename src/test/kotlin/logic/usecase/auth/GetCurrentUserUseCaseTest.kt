package logic.usecase.auth

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GetCurrentUserUseCaseTest {

    private val authRepository = mockk<AuthenticationRepository>()
    private val getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)

    @Test
    fun `should return success with user when user is authenticated`() {
        // arrange
        val currentUser = UserEntity(
            username = "testUser",
            password = "password123",
            type = UserType.MATE
        )
        every { authRepository.getCurrentUser() } returns Result.success(currentUser)

        // act
        val result = getCurrentUserUseCase()

        // assert
        assertTrue(result.isSuccess)
        assertEquals(currentUser, result.getOrNull())
        verify { authRepository.getCurrentUser() }
    }

    @Test
    fun `should return success with null when no user is authenticated`() {
        // arrange
        every { authRepository.getCurrentUser() } returns Result.success(null)

        // act
        val result = getCurrentUserUseCase()

        // assert
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
        verify { authRepository.getCurrentUser() }
    }

    @Test
    fun `should return failure when repository throws exception`() {
        // arrange
        val expectedError = RuntimeException("Failed to get current user")
        every { authRepository.getCurrentUser() } returns Result.failure(expectedError)

        // act
        val result = getCurrentUserUseCase()

        // assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
        assertEquals("Failed to get current user", result.exceptionOrNull()?.message)
        verify { authRepository.getCurrentUser() }
    }

    @Test
    fun `should return failure when session is invalid`() {
        // arrange
        val expectedError = PlanMateException.ValidationException("Invalid session")
        every { authRepository.getCurrentUser() } returns Result.failure(expectedError)

        // act
        val result = getCurrentUserUseCase()

        // assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is PlanMateException.ValidationException)
        assertEquals("Invalid session", result.exceptionOrNull()?.message)
        verify { authRepository.getCurrentUser() }
    }
}