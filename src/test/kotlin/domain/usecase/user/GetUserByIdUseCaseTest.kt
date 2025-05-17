package domain.usecase.user

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.entity.User
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.GetUserByIdUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.Test

class GetUserByIdUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUserByIdUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = GetUserByIdUseCase(repository)
    }

    @Test
    fun `should return UserEntity when repository finds by id`() = runTest {
        // Given
        val id = UUID.randomUUID()
        val expected = User(
            id = id,
            username = "jane",
            password = "jane123",
            type = UserType.MATE
        )
        coEvery { repository.getUserById(id) } returns expected

        // When
        val result = useCase(id)

        // Then
        assertEquals(expected, result)
        coVerify { repository.getUserById(id) }
    }

    @Test
    fun `should throw ItemNotFoundException when user not found`() = runTest {
        // Given
        val id = UUID.randomUUID()
        coEvery { repository.getUserById(id) } throws
                PlanMateException.ItemNotFoundException("User not found with id: $id")

        // When/Then
        val exception = assertThrows<PlanMateException.ItemNotFoundException> {
            useCase(id)
        }

        assert(exception.message?.contains("User not found") == true)
        coVerify { repository.getUserById(id) }
    }

    @Test
    fun `should throw exception when repository operation fails`() = runTest {
        // Given
        val id = UUID.randomUUID()
        val exception = RuntimeException("Database error")
        coEvery { repository.getUserById(id) } throws exception

        // When/Then
        val thrown = assertThrows<RuntimeException> {
            useCase(id)
        }

        assertEquals("Database error", thrown.message)
        coVerify { repository.getUserById(id) }
    }
}