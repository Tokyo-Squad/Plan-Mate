package logic.usecase.user

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.GetUsersUseCase
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class GetUsersUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUsersUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = GetUsersUseCase(repository)
    }

    @Test
    fun `should return empty list when repository returns empty`() {
        // Given
        val emptyList = emptyList<UserEntity>()
        every { repository.getUsers() } returns Result.success(emptyList)

        // When
        val result = useCase()

        // Then
        assertThat(result.getOrThrow()).isEmpty()
        verify { repository.getUsers() }
    }

    @Test
    fun `should return list of users when repository returns data`() {
        // Given
        val list = listOf(
            UserEntity(username = "username1", password = "password", type = UserType.MATE),
            UserEntity(username = "username2", password = "password", type = UserType.MATE)
        )
        every { repository.getUsers() } returns Result.success(list)

        // When
        val result = useCase()

        // Then
        assertThat(result.getOrThrow()).isEqualTo(list)
        verify { repository.getUsers() }
    }

    @Test
    fun `should fail when repository getUsers fails`() {
        // Given
        val exception = RuntimeException("Fetch error")
        every { repository.getUsers() } returns Result.failure(exception)

        // When
        val result = useCase()

        // Then
        assertThat(result.isFailure)
        verify { repository.getUsers() }
    }
}