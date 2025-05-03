package logic.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.auth.GetCurrentUserUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.Test


class GetCurrentUserUseCaseTest {
    private val authRepository = mockk<AuthenticationRepository>()
    private val getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)

    @Test
    fun `should return authenticated user when session is valid`() {
        val expectedUser = UserEntity(
            username = "testUser",
            password = "password123",
            type = UserType.MATE
        )
        every { authRepository.getCurrentUser() } returns Result.success(expectedUser)

        val result = getCurrentUserUseCase()

        assertThat(result.getOrNull()).isEqualTo(expectedUser)
    }

    @Test
    fun `should return null when no user is authenticated`() {
        every { authRepository.getCurrentUser() } returns Result.success(null)

        val result = getCurrentUserUseCase()

        assertThat(result.getOrNull()).isNull()
    }

    @Test
    fun `should propagate RuntimeException when repository fails`() {
        val expectedError = RuntimeException("Failed to get current user")
        every { authRepository.getCurrentUser() } returns Result.failure(expectedError)

        val result = getCurrentUserUseCase()

        assertThat(result.exceptionOrNull()).isEqualTo(expectedError)
    }

    @Test
    fun `should propagate ValidationException when session is invalid`() {
        val expectedError = PlanMateException.ValidationException("Invalid session")
        every { authRepository.getCurrentUser() } returns Result.failure(expectedError)

        val result = getCurrentUserUseCase()

        assertThat(result.exceptionOrNull()).isEqualTo(expectedError)
    }

    @Test
    fun `should call authRepository exactly once`() {
        every { authRepository.getCurrentUser() } returns Result.success(null)

        getCurrentUserUseCase()

        verify(exactly = 1) { authRepository.getCurrentUser() }
    }

    @Test
    fun `should propagate ItemNotFoundException when user not found`() {
        val expectedError = PlanMateException.ItemNotFoundException("User not found")
        every { authRepository.getCurrentUser() } returns Result.failure(expectedError)

        val result = getCurrentUserUseCase()

        assertThat(result.exceptionOrNull()).isEqualTo(expectedError)
    }
}