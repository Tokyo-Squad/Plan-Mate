package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import fakeData.StateFakeData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.WorkflowStateRepository
import org.example.logic.usecase.state.UpdateStateUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateWorkflowStateUseCaseTest {
    private lateinit var repo: WorkflowStateRepository
    private lateinit var useCase: UpdateStateUseCase
    private val fake = StateFakeData()

    @BeforeEach
    fun setUp() {
        repo = mockk()
        useCase = UpdateStateUseCase(repo)
    }

    @Test
    fun `invoke returns updated state when update is successful`() = runTest {
        // Given
        val oldState = fake.createState()
        val newState = oldState.copy(name = "Done")
        coEvery { repo.updateState(oldState.id, newState) } returns newState

        // When
        val result = useCase(oldState, newState)

        // Then
        assertThat(result).isEqualTo(newState)
        coVerify(exactly = 1) { repo.updateState(oldState.id, newState) }
    }

    @Test
    fun `invoke throws ItemNotFoundException when state is not found`() = runTest {
        // Given
        val oldState = fake.createState()
        val newState = oldState.copy(name = "Done")
        val ex = PlanMateException.ItemNotFoundException("State with ID ${oldState.id} not found")
        coEvery { repo.updateState(oldState.id, newState) } throws ex

        // When / Then
        val thrown = runCatching { useCase(oldState, newState) }.exceptionOrNull()
        assertThat(thrown).isSameInstanceAs(ex)
    }
}