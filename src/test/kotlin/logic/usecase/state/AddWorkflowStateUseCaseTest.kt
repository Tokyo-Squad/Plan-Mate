package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import fakeData.StateFakeData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.util.exception.FileException
import org.example.logic.repository.WorkflowStateRepository
import org.example.logic.usecase.state.AddStateUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow


class AddWorkflowStateUseCaseTest {
    private lateinit var repo: WorkflowStateRepository
    private lateinit var useCase: AddStateUseCase
    private val fake = StateFakeData()

    @BeforeEach
    fun setUp() {
        repo = mockk()
        useCase = AddStateUseCase(repo)
    }

    @Test
    fun `invoke state is successfully added when addition succeeds`() = runTest {
        // Given
        val state = fake.createState()
        coEvery { repo.addState(state) } returns Unit

        // Then & When
        assertDoesNotThrow { useCase(state) }
        coVerify(exactly = 1) { repo.addState(any()) }
    }

    @Test
    fun `invoke throws when repository throws FileWriteException`() = runTest {
        // Given
        val state = fake.createState()
        val ex = FileException.FileWriteException("disk full")
        coEvery { repo.addState(state) } throws ex

        // When / Then
        val thrown = runCatching { useCase(state) }.exceptionOrNull()
        assertThat(thrown).isSameInstanceAs(ex)
    }
}
