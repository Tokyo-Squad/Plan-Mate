package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.auth.LogoutUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LogoutUseCaseTest {
    private val authRepository = mockk<AuthenticationRepository>()
    private val logoutUseCase = LogoutUseCase(authRepository)

    @Test
    fun `should succeed when logout succeeds`() = runTest {
        // Given
        coEvery { authRepository.logout() } returns Unit

        // When/Then
        logoutUseCase()

        // Then
        coVerify(exactly = 1) { authRepository.logout() }
    }

    @Test
    fun `should throw RuntimeException when repository fails`() = runTest {
        // Given
        val expectedError = RuntimeException("Failed to logout")
        coEvery { authRepository.logout() } throws expectedError

        // When/Then
        val exception = assertThrows<RuntimeException> {
            logoutUseCase()
        }

        assertThat(exception).isEqualTo(expectedError)
        coVerify { authRepository.logout() }
    }

    @Test
    fun `should throw ValidationException when session is invalid`() = runTest {
        // Given
        val expectedError = PlanMateException.ValidationException("Invalid session")
        coEvery { authRepository.logout() } throws expectedError

        // When/Then
        val exception = assertThrows<PlanMateException.ValidationException> {
            logoutUseCase()
        }

        assertThat(exception).isEqualTo(expectedError)
        coVerify { authRepository.logout() }
    }
}