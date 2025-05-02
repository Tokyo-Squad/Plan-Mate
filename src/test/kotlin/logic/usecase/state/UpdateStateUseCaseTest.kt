package logic.usecase.state

import com.google.common.truth.Truth
import fakeData.StateFakeData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
    fun `invoke returns nested success on update`() {
        //Given
        val oldState = fake.createState()
        val newState = oldState.copy(name = "Done")
        every { repo.updateState(oldState, newState) } returns Result.success(newState)

        //When
        val outer = useCase(oldState, newState)

        //Then
        Truth.assertThat(outer.isSuccess).isTrue()
        val inner = outer.getOrNull()
        Truth.assertThat(inner).isNotNull()
        Truth.assertThat(inner!!.isSuccess).isTrue()
        Truth.assertThat(inner.getOrNull()).isEqualTo(newState)
        verify(exactly = 1) { repo.updateState(oldState, newState) }
    }

    @Test
    fun `invoke returns failure when repository throws ItemNotFoundException`() {
        //Given
        val oldState = fake.createState()
        val newState = oldState.copy(name = "Done")
        val ex = PlanMatException.ItemNotFoundException("not found")
        every { repo.updateState(oldState, newState) } throws ex

        //When
        val outer = useCase(oldState, newState)

        //Then
        Truth.assertThat(outer.isFailure).isTrue()
        Truth.assertThat(outer.exceptionOrNull()).isSameInstanceAs(ex)
    }
}