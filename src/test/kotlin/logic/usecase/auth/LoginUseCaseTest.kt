package logic.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.auth.LoginUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LoginUseCaseTest {
    private val authRepository = mockk<AuthenticationRepository>()
    private val loginUseCase = LoginUseCase(authRepository)

    @Test
    fun `should succeed when credentials are valid`() = runTest {
        // Given
        coEvery { authRepository.login("validUser", "validPassword") } returns Unit

        // When/Then
        loginUseCase("validUser", "validPassword")

        // Then
        coVerify(exactly = 1) { authRepository.login("validUser", "validPassword") }
    }

    @Test
    fun `should throw ItemNotFoundException when user does not exist`() = runTest {
        // Given
        val expectedError = PlanMateException.ItemNotFoundException("User not found.")
        coEvery { authRepository.login("nonExistingUser", any()) } throws expectedError

        // When/Then
        val exception = assertThrows<PlanMateException.ItemNotFoundException> {
            loginUseCase("nonExistingUser", "somePassword")
        }

        assertThat(exception).isEqualTo(expectedError)
        coVerify { authRepository.login("nonExistingUser", any()) }
    }

    @Test
    fun `should throw ValidationException when password is incorrect`() = runTest {
        // Given
        val expectedError = PlanMateException.ValidationException("Password is not correct.")
        coEvery { authRepository.login("validUser", "wrongPassword") } throws expectedError

        // When/Then
        val exception = assertThrows<PlanMateException.ValidationException> {
            loginUseCase("validUser", "wrongPassword")
        }

        assertThat(exception).isEqualTo(expectedError)
        coVerify { authRepository.login("validUser", "wrongPassword") }
    }

    @Test
    fun `should throw RuntimeException when repository fails unexpectedly`() = runTest {
        // Given
        val expectedError = RuntimeException("Network error")
        coEvery { authRepository.login(any(), any()) } throws expectedError

        // When/Then
        val exception = assertThrows<RuntimeException> {
            loginUseCase("validUser", "validPassword")
        }

        assertThat(exception).isEqualTo(expectedError)
        coVerify { authRepository.login("validUser", "validPassword") }
    }

    @Test
    fun `should throw ValidationException when username is empty`() = runTest {
        // Given
        // When/Then
        val exception = assertThrows<PlanMateException.ValidationException> {
            loginUseCase("", "anyPassword")
        }

        assertThat(exception.message).contains("Username cannot be empty")
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }
}
