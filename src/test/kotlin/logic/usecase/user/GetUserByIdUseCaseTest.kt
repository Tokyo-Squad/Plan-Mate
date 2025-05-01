package logic.usecase.user

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.entity.UserEntity
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.GetUserByIdUseCase
import org.junit.jupiter.api.BeforeEach
import java.util.UUID
import kotlin.test.Test

class GetUserByIdUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUserByIdUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = GetUserByIdUseCase(repository)
    }

    @Test
    fun `should return UserEntity when repository finds by id`() {
        // Given
        val id = UUID.randomUUID()
        val expected = UserEntity(id, "jane", "jane@example.com", type = UserType.MATE)
        every { repository.getUserById(id) } returns Result.success(expected)

        // When
        val result = useCase(id)

        // Then
        assertThat(result.getOrThrow()).isEqualTo(expected)
        verify { repository.getUserById(id) }
    }


    @Test
    fun `should fail when repository not finds by id`() {
        // Given
        val id = UUID.randomUUID()
        every { repository.getUserById(id) } returns Result.failure(Exception())

        // When
        val result = useCase(id)

        // Then
        assertThat(result.isFailure)
        verify { repository.getUserById(id) }
    }

    @Test
    fun `should fail when repository getUserById fails`() {
        // Given
        val id = UUID.randomUUID()

        val exception = RuntimeException("Fetch error")
        every { repository.getUserById(id) } returns Result.failure(exception)

        // When
        val result = useCase(id)

        // Then
        assertThat(result.isFailure)
        verify { repository.getUserById(id) }
    }
}