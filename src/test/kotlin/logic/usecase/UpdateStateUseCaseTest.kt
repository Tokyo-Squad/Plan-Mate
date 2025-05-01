package logic.usecase

import fakeData.StateFakeData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.UpdateStateUseCase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class UpdateStateUseCaseTest {
    private lateinit var repository: StateRepository
    private lateinit var useCase: UpdateStateUseCase
    private val fakeData = StateFakeData()

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = UpdateStateUseCase(repository)
    }

    @Test fun `invoke returns success on update`() {
        // Given
        val oldState = fakeData.createState()
        val newState = oldState.copy(name = "Done")

        every { repository.updateState(oldState, newState) } returns newState

        // When
        val result = useCase(oldState, newState)

        // Then
        assertTrue(result.isSuccess)
        verify(exactly = 1) { repository.updateState(oldState, newState) }
    }

    @Test fun `invoke returns failure when repository throws`() {
        // Given
        val oldState = fakeData.createState()
        val newState = oldState.copy(name = "Done")
        val ex = IllegalArgumentException("not found")
        every { repository.updateState(oldState, newState) } throws ex

        // When
        val result = useCase(oldState, newState)

        // Then
        assertTrue(result.isFailure)
        assertEquals(ex, result.exceptionOrNull())
    }

}