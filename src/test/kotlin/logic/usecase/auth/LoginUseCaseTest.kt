package logic.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.auth.LoginUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.Test


class LoginUseCaseTest {
    private val authRepository = mockk<AuthenticationRepository>()
    private val loginUseCase = LoginUseCase(authRepository)

    @Test
    fun `should return Success(Unit) when credentials are valid`() {
        every { authRepository.login("validUser", "validPassword") } returns Result.success(Unit)

        val result = loginUseCase("validUser", "validPassword")

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `should propagate ItemNotFoundException when user does not exist`() {
        val expectedError = PlanMateException.ItemNotFoundException("User not found.")
        every { authRepository.login("nonExistingUser", any()) } returns Result.failure(expectedError)

        val result = loginUseCase("nonExistingUser", "somePassword")

        assertThat(result.exceptionOrNull()).isEqualTo(expectedError)
    }

    @Test
    fun `should propagate ValidationException when password is incorrect`() {
        val expectedError = PlanMateException.ValidationException("Password is not correct.")
        every { authRepository.login("validUser", "wrongPassword") } returns Result.failure(expectedError)

        val result = loginUseCase("validUser", "wrongPassword")

        assertThat(result.exceptionOrNull()).isEqualTo(expectedError)
    }

    @Test
    fun `should propagate RuntimeException when repository fails unexpectedly`() {
        val expectedError = RuntimeException("Network error")
        every { authRepository.login(any(), any()) } returns Result.failure(expectedError)

        val result = loginUseCase("validUser", "validPassword")

        assertThat(result.exceptionOrNull()).isEqualTo(expectedError)
    }

    @Test
    fun `should call authRepository with exact credentials once`() {
        every { authRepository.login("validUser", "validPassword") } returns Result.success(Unit)

        loginUseCase("validUser", "validPassword")

        verify(exactly = 1) { authRepository.login("validUser", "validPassword") }


    }

    @Test
    fun `should propagate ValidationException when username is empty`() {
        val expectedError = PlanMateException.ValidationException("Username cannot be empty.")
        every { authRepository.login("", any()) } returns Result.failure(expectedError)

        val result = loginUseCase("", "anyPassword")

        assertThat(result.exceptionOrNull()).isEqualTo(expectedError)
    }
}