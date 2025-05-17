package domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.entity.User
import org.example.entity.UserType
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.auth.RegisterUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RegisterUseCaseTest {
    private val authRepository = mockk<AuthenticationRepository>()
    private val registerUseCase = RegisterUseCase(authRepository)
    private val adminUser = User(
        username = "admin",
        password = "adminPass",
        type = UserType.ADMIN
    )
    private val mateUser = User(
        username = "mate",
        password = "matePass",
        type = UserType.MATE
    )
    private val validNewUser = User(
        username = "newUser",
        password = "password123",
        type = UserType.MATE
    )

    @Test
    fun `should succeed when admin creates new user`() = runTest {
        // Given
        coEvery { authRepository.register(validNewUser) } returns Unit

        // When/Then
        registerUseCase(validNewUser, adminUser)

        // Then
        coVerify { authRepository.register(validNewUser) }
    }

    @Test
    fun `should throw exception when MATE user tries to create user`() = runTest {
        // When/Then
        val exception = assertThrows<PlanMateException.UserActionNotAllowedException> {
            registerUseCase(validNewUser, mateUser)
        }

        assertThat(exception.message).contains("MATE users cannot create new users")
        coVerify(exactly = 0) { authRepository.register(any()) }
    }

    @Test
    fun `should throw ValidationException when username exists`() = runTest {
        // Given
        val error = PlanMateException.ValidationException("Username exists")
        coEvery { authRepository.register(validNewUser) } throws error

        // When/Then
        val exception = assertThrows<PlanMateException.ValidationException> {
            registerUseCase(validNewUser, adminUser)
        }

        assertThat(exception).isEqualTo(error)
        coVerify { authRepository.register(validNewUser) }
    }

    @Test
    fun `should throw repository exceptions`() = runTest {
        // Given
        val error = RuntimeException("DB error")
        coEvery { authRepository.register(validNewUser) } throws error

        // When/Then
        val exception = assertThrows<RuntimeException> {
            registerUseCase(validNewUser, adminUser)
        }

        assertThat(exception).isEqualTo(error)
        coVerify { authRepository.register(validNewUser) }
    }

    @Test
    fun `should throw ValidationException for invalid user data`() = runTest {
        // Given
        val invalidUser = validNewUser.copy(username = "")

        // When/Then
        val exception = assertThrows<PlanMateException.ValidationException> {
            registerUseCase(invalidUser, adminUser)
        }

        assertThat(exception.message).contains("Username cannot be empty")
        coVerify(exactly = 0) { authRepository.register(any()) }
    }
}
