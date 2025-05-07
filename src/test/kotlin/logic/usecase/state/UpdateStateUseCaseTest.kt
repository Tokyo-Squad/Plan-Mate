package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import fakeData.StateFakeData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.UpdateStateUseCase
import org.example.utils.PlanMatException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateStateUseCaseTest {
    private lateinit var repo: StateRepository
    private lateinit var useCase: UpdateStateUseCase
    private val fake = StateFakeData()

    @BeforeEach
    fun setUp() {
        repo = mockk(relaxed = true)
        useCase = UpdateStateUseCase(repo)
    }

    @Test
    fun `invoke returns nested success on update`() = runTest {
        // Given
        val oldState = fake.createState()
        val newState = oldState.copy(name = "Done")
        coEvery { repo.updateState(oldState, newState) } returns Result.success(newState)

        // When
        val outer = useCase(oldState, newState)

        // Then
        assertThat(outer.isSuccess).isTrue()
        val inner = outer.getOrNull()
        assertThat(inner).isNotNull()
        assertThat(inner!!.isSuccess).isTrue()
        assertThat(inner.getOrNull()).isEqualTo(newState)
        coVerify(exactly = 1) { repo.updateState(oldState, newState) }
    }

    @Test
    fun `invoke returns failure when repository throws ItemNotFoundException`() = runTest {
        // Given
        val oldState = fake.createState()
        val newState = oldState.copy(name = "Done")
        val ex = PlanMatException.ItemNotFoundException("not found")
        coEvery { repo.updateState(oldState, newState) } throws ex

        // When
        val outer = useCase(oldState, newState)

        // Then
        assertThat(outer.isFailure).isTrue()
        assertThat(outer.exceptionOrNull()).isSameInstanceAs(ex)
    }
}
