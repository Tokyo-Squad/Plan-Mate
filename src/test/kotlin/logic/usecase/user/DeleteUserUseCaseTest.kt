package logic.usecase.user

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.entity.User
import org.example.entity.UserType
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.user.DeleteUserUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.Test

class DeleteUserUseCaseTest {
    private lateinit var repository: UserRepository
    private lateinit var useCase: DeleteUserUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = DeleteUserUseCase(repository)
    }

    @Test
    fun `should succeed when non-MATE user deletes user`() = runTest {
        // Given
        val id = UUID.randomUUID()
        val adminUser = User(
            username = "admin",
            password = "pwd",
            type = UserType.ADMIN
        )

        // When
        useCase(id, adminUser)

        // Then
        coVerify { repository.delete(id) }
    }

    @Test
    fun `should throw exception when MATE user deletes user`() = runTest {
        // Given
        val id = UUID.randomUUID()
        val mateUser = User(
            username = "mate",
            password = "pwd",
            type = UserType.MATE
        )

        // When/Then
        val exception = assertThrows<PlanMateException.UserActionNotAllowedException> {
            useCase(id, mateUser)
        }

        assert(exception.message?.contains("MATE users are not allowed") == true)
        coVerify(exactly = 0) { repository.delete(any()) }
    }

    @Test
    fun `should throw exception when repository delete fails`() = runTest {
        // Given
        val id = UUID.randomUUID()
        val adminUser = User(
            username = "admin",
            password = "pwd",
            type = UserType.ADMIN
        )
        val exception = RuntimeException("Deletion error")
        coEvery { repository.delete(id) } throws exception

        // When/Then
        val thrown = assertThrows<RuntimeException> {
            useCase(id, adminUser)
        }

        assert(thrown.message == "Deletion error")
        coVerify { repository.delete(id) }
    }
}