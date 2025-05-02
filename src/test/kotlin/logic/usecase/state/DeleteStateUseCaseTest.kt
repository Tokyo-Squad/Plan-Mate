package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import fakeData.StateFakeData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.DeleteStateUseCase
import org.example.utils.PlanMatException
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class DeleteStateUseCaseTest {

    private lateinit var repo: StateRepository
    private lateinit var useCase: DeleteStateUseCase
    private val fake = StateFakeData()

    @BeforeEach fun setUp() {
        repo = mockk(relaxed = true)
        useCase = DeleteStateUseCase(repo)
    }

    @Test fun `invoke returns success when deletion succeeds`() {
        //Given
        val state = fake.createState()
        every { repo.deleteState(state) } returns Result.success(true)

        //When
        val outer = useCase(state)

        //Then
        assertThat(outer.isSuccess).isTrue()
        val inner = outer.getOrNull()
        assertThat(inner).isNotNull()
        assertThat(inner!!.isSuccess).isTrue()
        assertThat(inner.getOrNull()).isTrue()
        verify(exactly = 1) { repo.deleteState(state) }
    }

    @Test fun `invoke returns failure when repository throws ItemNotFoundException`() {
        //Given
        val state = fake.createState()
        val ex = PlanMatException.ItemNotFoundException("not found")
        every { repo.deleteState(state) } throws ex

        //When
        val outer = useCase(state)

        //Then
        assertThat(outer.isFailure).isTrue()
        assertThat(outer.exceptionOrNull()).isSameInstanceAs(ex)
    }
  
}