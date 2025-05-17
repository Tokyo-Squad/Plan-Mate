package logic.usecase.state


import com.google.common.truth.Truth.assertThat
import fakeData.StateFakeData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.WorkflowStateRepository
import org.example.logic.usecase.state.GetStateByIdUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.BeforeEach
import java.util.*
import kotlin.test.Test

class GetWorkflowStateByIdUseCaseTest {
    private lateinit var repo: WorkflowStateRepository
    private lateinit var useCase: GetStateByIdUseCase
    private val fake = StateFakeData()

    @BeforeEach
    fun setUp() {
        repo = mockk()
        useCase = GetStateByIdUseCase(repo)
    }

    @Test
    fun `invoke returns state when valid id is entered`() = runTest {
        // Given
        val id = UUID.randomUUID()
        val expected = fake.createState(id = id)
        coEvery { repo.getStateById(id) } returns expected

        // When
        val result = useCase(id)

        // Then
        assertThat(result).isEqualTo(expected)
        coVerify(exactly = 1) { repo.getStateById(id) }
    }

    @Test
    fun `invoke throws ItemNotFoundException when state is not found`() = runTest {
        // Given
        val id = UUID.randomUUID()
        val ex = PlanMateException.ItemNotFoundException("State with ID $id not found")
        coEvery { repo.getStateById(id) } throws ex

        // When / Then
        val thrown = runCatching { useCase(id) }.exceptionOrNull()
        assertThat(thrown).isSameInstanceAs(ex)
    }
}