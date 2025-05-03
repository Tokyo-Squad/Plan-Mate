package logic.usecase.state

import com.google.common.truth.Truth
import fakeData.StateFakeData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
        repo = mockk(relaxed = true)
        useCase = AddStateUseCase(repo)
    }

    @Test
    fun `invoke returns success when state successfully added`() {
        //Given
        val state = fake.createState()
        val expectedId = state.id.toString()
        every { repo.addState(state) } returns Result.success(expectedId)

        //When
        val outer = useCase(state)

        //Then
        Truth.assertThat(outer.isSuccess).isTrue()
        val inner = outer.getOrNull()
        Truth.assertThat(inner).isNotNull()
        Truth.assertThat(inner!!.isSuccess).isTrue()
        Truth.assertThat(inner.getOrNull()).isEqualTo(expectedId)
        verify(exactly = 1) { repo.addState(state) }
    }

    @Test
    fun `invoke returns failure when repository throws FileWriteException`() {
        //Given
        val state = fake.createState()
        val ex = PlanMateException.FileWriteException("disk full")
        every { repo.addState(state) } throws ex

        //When
        val outer = useCase(state)

        //Then
        Truth.assertThat(outer.isFailure).isTrue()
        Truth.assertThat(outer.exceptionOrNull()).isSameInstanceAs(ex)
    }

}