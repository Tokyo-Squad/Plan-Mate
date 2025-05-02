package logic.usecase.auth

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.usecase.auth.RegisterUseCase
import org.example.utils.PlanMatException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RegisterUseCaseTest {

    private val authRepository = mockk<AuthenticationRepository>()
    private val registerUseCase = RegisterUseCase(authRepository)

    @Test
    fun `should return success when admin creates new user`() {
        // arrange
        val newUser = UserEntity(
            username = "newUser",
            password = "password123",
            type = UserType.MATE
        )
        val adminUser = UserEntity(
            username = "admin",
            password = "adminPass",
            type = UserType.ADMIN
        )
        every { authRepository.register(newUser, adminUser) } returns Result.success(Unit)

        // act
        val result = registerUseCase(newUser, adminUser)

        // assert
        assertTrue(result.isSuccess)
        verify { authRepository.register(newUser, adminUser) }
    }

    @Test
    fun `should return failure when MATE user tries to create new user`() {
        // arrange
        val newUser = UserEntity(
            username = "newUser",
            password = "password123",
            type = UserType.MATE
        )
        val mateUser = UserEntity(
            username = "mate",
            password = "matePass",
            type = UserType.MATE
        )

        // act
        val result = registerUseCase(newUser, mateUser)

        // assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is PlanMatException.UserActionNotAllowedException)
        assertEquals(
            "MATE users cannot create new users.",
            result.exceptionOrNull()?.message
        )
        verify(exactly = 0) { authRepository.register(any(), any()) }
    }

    @Test
    fun `should return failure when username already exists`() {
        // arrange
        val newUser = UserEntity(
            username = "existingUser",
            password = "password123",
            type = UserType.MATE
        )
        val adminUser = UserEntity(
            username = "admin",
            password = "adminPass",
            type = UserType.ADMIN
        )
        val expectedError = PlanMatException.ValidationException("A user with that username already exists.")
        every { authRepository.register(newUser, adminUser) } returns Result.failure(expectedError)

        // act
        val result = registerUseCase(newUser, adminUser)

        // assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is PlanMatException.ValidationException)
        assertEquals(
            "A user with that username already exists.",
            result.exceptionOrNull()?.message
        )
        verify { authRepository.register(newUser, adminUser) }
    }

    @Test
    fun `should return failure when repository throws exception`() {
        // arrange
        val newUser = UserEntity(
            username = "newUser",
            password = "password123",
            type = UserType.MATE
        )
        val adminUser = UserEntity(
            username = "admin",
            password = "adminPass",
            type = UserType.ADMIN
        )
        val expectedError = RuntimeException("Failed to register user")
        every { authRepository.register(newUser, adminUser) } returns Result.failure(expectedError)

        // act
        val result = registerUseCase(newUser, adminUser)

        // assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
        assertEquals("Failed to register user", result.exceptionOrNull()?.message)
        verify { authRepository.register(newUser, adminUser) }
    }

    @Test
    fun `should return failure when new user data is invalid`() {
        // arrange
        val newUser = UserEntity(
            username = "",  // Invalid: empty username
            password = "password123",
            type = UserType.MATE
        )
        val adminUser = UserEntity(
            username = "admin",
            password = "adminPass",
            type = UserType.ADMIN
        )
        val expectedError = PlanMatException.ValidationException("Username cannot be empty")
        every { authRepository.register(newUser, adminUser) } returns Result.failure(expectedError)

        // act
        val result = registerUseCase(newUser, adminUser)

        // assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is PlanMatException.ValidationException)
        assertEquals("Username cannot be empty", result.exceptionOrNull()?.message)
        verify { authRepository.register(newUser, adminUser) }
    }
}