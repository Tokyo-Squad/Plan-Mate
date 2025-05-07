package logic.usecase.state

import com.google.common.truth.Truth
import fakeData.StateFakeData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.AddStateUseCase
import org.example.utils.PlanMatException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddStateUseCaseTest {
    private lateinit var repo: StateRepository
    private lateinit var useCase: AddStateUseCase
    private val fake = StateFakeData()

    @BeforeEach
    fun setUp() {
        repo = mockk(relaxed = true)
        useCase = AddStateUseCase(repo)
    }

    @Test
    fun `invoke returns state ID when state successfully added`() = runTest {
        val state = fake.createState()
        val expectedId = state.id.toString()


        coEvery { repo.addState(state) } returns Result.success(expectedId)

        val result = useCase(state)


        Truth.assertThat(result.isSuccess).isTrue()
        Truth.assertThat(result.getOrNull()).isEqualTo(expectedId)

        coVerify(exactly = 1) { repo.addState(state) }
    }

    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        val state = fake.createState()
        val ex = PlanMatException.FileWriteException("disk full")
        coEvery { repo.addState(state) } throws ex

        try {
            useCase(state)
            assert(false) { "Expected exception was not thrown" }
        } catch (e: Exception) {
            Truth.assertThat(e).isSameInstanceAs(ex)
        }
    }
}
