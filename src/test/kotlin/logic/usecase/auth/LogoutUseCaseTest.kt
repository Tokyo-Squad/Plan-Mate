package logic.usecase.auth

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.auth.LogoutUseCase
import org.example.utils.PlanMatException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LogoutUseCaseTest {

    private val authRepository = mockk<AuthenticationRepository>()
    private val logoutUseCase = LogoutUseCase(authRepository)

    @Test
    fun `should return success when logout is successful`() {
        // arrange
        every { authRepository.logout() } returns Result.success(Unit)

        // act
        val result = logoutUseCase()

        // assert
        assertTrue(result.isSuccess)
        verify { authRepository.logout() }
    }

    @Test
    fun `should return failure when repository throws exception`() {
        // arrange
        val expectedError = RuntimeException("Failed to logout")
        every { authRepository.logout() } returns Result.failure(expectedError)

        // act
        val result = logoutUseCase()

        // assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
        assertEquals("Failed to logout", result.exceptionOrNull()?.message)
        verify { authRepository.logout() }
    }

    @Test
    fun `should return failure when user session is invalid`() {
        // arrange
        val expectedError = PlanMatException.ValidationException("Invalid session")
        every { authRepository.logout() } returns Result.failure(expectedError)

        // act
        val result = logoutUseCase()

        // assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is PlanMatException.ValidationException)
        assertEquals("Invalid session", result.exceptionOrNull()?.message)
        verify { authRepository.logout() }
    }
}