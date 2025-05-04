package logic.usecase.state


import com.google.common.truth.Truth.assertThat
import fakeData.StateFakeData
import io.mockk.every
import io.mockk.mockk
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.state.GetStateByIdUseCase
import org.example.utils.PlanMateException
import org.junit.jupiter.api.BeforeEach
import java.util.UUID
import kotlin.test.Test

class GetStateByIdUseCaseTest {
    private lateinit var repo: StateRepository
    private lateinit var useCase: GetStateByIdUseCase
    private val fake = StateFakeData()

    @BeforeEach fun setUp() {
        repo = mockk()
        useCase = GetStateByIdUseCase(repo)
    }


    @Test fun `invoke returns state when valid id is entered`() {
        //Given
        val id = UUID.randomUUID()
        val expected = fake.createState(id = id)
        every { repo.getStateById(id) } returns Result.success(expected)

        //When
        val result = useCase(id)

        //Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(expected)
    }

    @Test fun `invoke returns failure when repository throws ItemNotFoundException`() {
        //Given
        val id = UUID.randomUUID()
        val ex = PlanMateException.ItemNotFoundException("State with ID $id not found")
        every { repo.getStateById(id) } throws ex

        //When
        val result = useCase(id)

        //Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isSameInstanceAs(ex)
    }
}