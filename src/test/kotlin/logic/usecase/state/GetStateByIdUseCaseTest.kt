package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import fakeData.StateFakeData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.GetStateByIdUseCase
import org.example.utils.PlanMatException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class GetStateByIdUseCaseTest {
    private lateinit var repo: StateRepository
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
        coEvery { repo.getStateById(id) } returns Result.success(expected)

        // When
        val result = useCase(id)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expected)
        coVerify(exactly = 1) { repo.getStateById(id) }
    }

    @Test
    fun `invoke returns failure when repository throws ItemNotFoundException`() = runTest {
        // Given
        val id = UUID.randomUUID()
        val ex = PlanMatException.ItemNotFoundException("State with ID $id not found")
        coEvery { repo.getStateById(id) } throws ex

        // When
        val result = useCase(id)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(ex)
    }
}
