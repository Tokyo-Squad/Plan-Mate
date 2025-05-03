package logic.usecase.auth

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.auth.LoginUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LoginUseCaseTest {

    private val authRepository = mockk<AuthenticationRepository>()
    private val loginUseCase = LoginUseCase(authRepository)

    @Test
    fun `should return success when credentials are valid`() {
        // arrange
        val username = "validUser"
        val password = "validPassword"
        every { authRepository.login(username, password) } returns Result.success(Unit)

        // act
        val result = loginUseCase(username, password)

        // assert
        assertTrue(result.isSuccess)
        verify { authRepository.login(username, password) }
    }

    @Test
    fun `should return failure when user not found`() {
        // arrange
        val username = "nonExistingUser"
        val password = "somePassword"
        val expectedError = PlanMateException.ItemNotFoundException("User not found.")
        every { authRepository.login(username, password) } returns Result.failure(expectedError)

        // act
        val result = loginUseCase(username, password)

        // assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is PlanMateException.ItemNotFoundException)
        assertEquals("User not found.", result.exceptionOrNull()?.message)
        verify { authRepository.login(username, password) }
    }

    @Test
    fun `should return failure when password is incorrect`() {
        // arrange
        val username = "validUser"
        val password = "wrongPassword"
        val expectedError = PlanMateException.ValidationException("Password is not correct.")
        every { authRepository.login(username, password) } returns Result.failure(expectedError)

        // act
        val result = loginUseCase(username, password)

        // assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is PlanMateException.ValidationException)
        assertEquals("Password is not correct.", result.exceptionOrNull()?.message)
        verify { authRepository.login(username, password) }
    }

    @Test
    fun `should return failure when repository throws exception`() {
        // arrange
        val username = "validUser"
        val password = "validPassword"
        val expectedError = RuntimeException("Network error")
        every { authRepository.login(username, password) } returns Result.failure(expectedError)

        // act
        val result = loginUseCase(username, password)

        // assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        verify { authRepository.login(username, password) }
    }
}