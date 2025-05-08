package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import fakeData.StateFakeData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.state.AddStateUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class AddStateUseCaseTest {
    private lateinit var repo: StateRepository
    private lateinit var useCase: AddStateUseCase
    private val fake = StateFakeData()

    @BeforeEach
    fun setUp() {
        repo = mockk()
        useCase = AddStateUseCase(repo)
    }

    @Test
    fun `invoke returns state ID when state is successfully added`() = runTest {
        // Given
        val state = fake.createState()
        val expectedId = state.id.toString()
        coEvery { repo.addState(state) } returns expectedId

        // When
        val result = useCase(state)

        // Then
        assertThat(result).isEqualTo(expectedId)
        coVerify(exactly = 1) { repo.addState(state) }
    }

    @Test
    fun `invoke throws when repository throws FileWriteException`() = runTest {
        // Given
        val state = fake.createState()
        val ex = PlanMateException.FileWriteException("disk full")
        coEvery { repo.addState(state) } throws ex

        // When / Then
        val thrown = runCatching { useCase(state) }.exceptionOrNull()
        assertThat(thrown).isSameInstanceAs(ex)
    }
}
