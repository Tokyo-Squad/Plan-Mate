package logic.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.auth.LogoutUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.Test

class LogoutUseCaseTest {
    private val authRepository = mockk<AuthenticationRepository>()
    private val logoutUseCase = LogoutUseCase(authRepository)

    @Test
    fun `should return Success(Unit) when logout succeeds`() {
        every { authRepository.logout() } returns Result.success(Unit)

        val result = logoutUseCase()

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `should propagate RuntimeException when repository fails`() {
        val expectedError = RuntimeException("Failed to logout")
        every { authRepository.logout() } returns Result.failure(expectedError)

        val result = logoutUseCase()

        assertThat(result.exceptionOrNull()).isEqualTo(expectedError)
    }

    @Test
    fun `should propagate ValidationException when session is invalid`() {
        val expectedError = PlanMateException.ValidationException("Invalid session")
        every { authRepository.logout() } returns Result.failure(expectedError)

        val result = logoutUseCase()

        assertThat(result.exceptionOrNull()).isEqualTo(expectedError)
    }

    @Test
    fun `should call authRepository exactly once when executed`() {
        every { authRepository.logout() } returns Result.success(Unit)

        logoutUseCase()

        verify(exactly = 1) { authRepository.logout() }
    }
}