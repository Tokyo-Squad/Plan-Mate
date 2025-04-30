package logic.usecase.user

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.GetUserByUsernameUseCase
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test


class GetUserByUsernameUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUserByUsernameUseCase


    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = GetUserByUsernameUseCase(repository)
    }

    @Test
    fun `should return UserEntity when repository finds by username`() {
        // Given
        val expected = UserEntity(username = "john", password = "john@example.com", type = UserType.MATE)
        every { repository.getUserByUsername("john") } returns Result.success(expected)

        // When
        val result = useCase("john")

        // Then
        assertThat(result.isSuccess)
        verify { repository.getUserByUsername("john") }
    }

    @Test
    fun `should fail when username is empty`() {
        // Given
        val emptyUsername = ""

        // When
        val result = useCase(emptyUsername)

        // Then
        assertThat(result.isFailure)
        verify(exactly = 0) { repository.getUserByUsername(any()) }
    }

    @Test
    fun `should fail when repository getUserByUsername fails`() {
        // Given
        val username = "username"

        val exception = RuntimeException("Fetch error")
        every { repository.getUserByUsername(username) } returns Result.failure(exception)

        // When
        val result = useCase(username)

        // Then
        assertThat(result.isFailure)
        verify { repository.getUserByUsername(username) }
    }
}
