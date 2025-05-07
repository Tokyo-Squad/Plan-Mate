package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import fakeData.StateFakeData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.DeleteStateUseCase
import org.example.utils.PlanMatException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeleteStateUseCaseTest {

    private lateinit var repo: StateRepository
    private lateinit var useCase: DeleteStateUseCase
    private val fake = StateFakeData()

    @BeforeEach
    fun setUp() {
        repo = mockk(relaxed = true)
        useCase = DeleteStateUseCase(repo)
    }

    @Test
    fun `invoke returns success when deletion succeeds`() = runTest {
        // Given
        val state = fake.createState()
        val expected = Result.success(true)
        coEvery { repo.deleteState(state) } returns expected

        // When
        val result = useCase(state)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
        coVerify(exactly = 1) { repo.deleteState(state) }
    }

    @Test
    fun `invoke returns failure when repository throws ItemNotFoundException`() = runTest {
        // Given
        val state = fake.createState()
        val ex = PlanMatException.ItemNotFoundException("not found")
        coEvery { repo.deleteState(state) } throws ex

        // When
        try {
            useCase(state)
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            assertThat(e).isSameInstanceAs(ex)
        }
    }
}
