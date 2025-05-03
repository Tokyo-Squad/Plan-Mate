package logic.usecase.auth

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.auth.RegisterUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RegisterUseCaseTest {
    private val authRepository = mockk<AuthenticationRepository>()
    private val registerUseCase = RegisterUseCase(authRepository)
    private val adminUser = UserEntity(
        username = "admin",
        password = "adminPass",
        type = UserType.ADMIN
    )
    private val mateUser = UserEntity(
        username = "mate",
        password = "matePass",
        type = UserType.MATE
    )
    private val validNewUser = UserEntity(
        username = "newUser",
        password = "password123",
        type = UserType.MATE
    )

    @Test
    fun `should return success when admin creates new user`() {
        every { authRepository.register(validNewUser, adminUser) } returns Result.success(Unit)
        assertTrue(registerUseCase(validNewUser, adminUser).isSuccess)
    }

    @Test
    fun `should call repository when admin creates user`() {
        every { authRepository.register(validNewUser, adminUser) } returns Result.success(Unit)
        registerUseCase(validNewUser, adminUser)
        verify { authRepository.register(validNewUser, adminUser) }
    }

    @Test
    fun `should return failure when MATE user tries to create user`() {
        assertTrue(registerUseCase(validNewUser, mateUser).isFailure)
    }

    @Test
    fun `should return UserActionNotAllowedException when MATE creates user`() {
        assertThat(registerUseCase(validNewUser, mateUser).exceptionOrNull())
            .isInstanceOf(PlanMateException.UserActionNotAllowedException::class.java)
    }

    @Test
    fun `should prevent repository call when MATE creates user`() {
        registerUseCase(validNewUser, mateUser)
        verify(exactly = 0) { authRepository.register(any(), any()) }
    }

    @Test
    fun `should return failure when username exists`() {
        val error = PlanMateException.ValidationException("Username exists")
        every { authRepository.register(validNewUser, adminUser) } returns Result.failure(error)
        assertTrue(registerUseCase(validNewUser, adminUser).isFailure)
    }

    @Test
    fun `should propagate ValidationException for existing username`() {
        val error = PlanMateException.ValidationException("Username exists")
        every { authRepository.register(validNewUser, adminUser) } returns Result.failure(error)
        assertThat(registerUseCase(validNewUser, adminUser).exceptionOrNull())
            .isEqualTo(error)
    }

    @Test
    fun `should propagate repository exceptions`() {
        val error = RuntimeException("DB error")
        every { authRepository.register(validNewUser, adminUser) } returns Result.failure(error)
        assertThat(registerUseCase(validNewUser, adminUser).exceptionOrNull())
            .isEqualTo(error)
    }

    @Test
    fun `should return failure for invalid new user data`() {
        val invalidUser = validNewUser.copy(username = "")
        val error = PlanMateException.ValidationException("Invalid username")
        every { authRepository.register(invalidUser, adminUser) } returns Result.failure(error)
        assertTrue(registerUseCase(invalidUser, adminUser).isFailure)
    }

    @Test
    fun `should propagate ValidationException for invalid data`() {
        val invalidUser = validNewUser.copy(username = "")
        val error = PlanMateException.ValidationException("Invalid username")
        every { authRepository.register(invalidUser, adminUser) } returns Result.failure(error)
        assertThat(registerUseCase(invalidUser, adminUser).exceptionOrNull())
            .isEqualTo(error)
    }
}