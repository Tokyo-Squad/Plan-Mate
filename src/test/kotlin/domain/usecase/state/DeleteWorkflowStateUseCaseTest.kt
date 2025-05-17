package domain.usecase.state

import com.google.common.truth.Truth.assertThat
import fakeData.StateFakeData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.WorkflowStateRepository
import org.example.logic.usecase.state.DeleteStateUseCase
import domain.utils.exception.PlanMateException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test

class DeleteWorkflowStateUseCaseTest {

    private lateinit var repo: WorkflowStateRepository
    private lateinit var useCase: DeleteStateUseCase
    private val fake = StateFakeData()

    @BeforeEach
    fun setUp() {
        repo = mockk()
        useCase = DeleteStateUseCase(repo)
    }

    @Test
    fun `invoke successfully when deletion succeeds`() = runTest {
        // Given
        val state = fake.createState()
        coEvery { repo.deleteState(state.id) } returns Unit

        // When & Then
        assertDoesNotThrow{useCase(state)}
        coVerify(exactly = 1) { repo.deleteState(state.id) }
    }

    @Test
    fun `invoke throws when repository throws ItemNotFoundException`() = runTest {
        // Given
        val state = fake.createState()
        val ex = PlanMateException.ItemNotFoundException("not found")
        coEvery { repo.deleteState(state.id) } throws ex

        // When / Then
        val thrown = runCatching { useCase(state) }.exceptionOrNull()
        assertThat(thrown).isSameInstanceAs(ex)
    }
}
