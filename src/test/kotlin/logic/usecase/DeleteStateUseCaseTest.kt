package logic.usecase

import fakeData.StateFakeData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.repository.StateRepository
import org.example.logic.usecase.DeleteStateUseCase
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class DeleteStateUseCaseTest {
    private lateinit var stateRepository: StateRepository
    private lateinit var deleteStateUseCase: DeleteStateUseCase
    private val fakeData = StateFakeData()

    @BeforeTest fun setUp() {
        stateRepository = mockk(relaxed = true)
        deleteStateUseCase = DeleteStateUseCase(stateRepository)
    }

    @Test fun `invoke returns true on successful delete`() {
        // Given
        val state = fakeData.createState()
        every { stateRepository.deleteState(state) } returns true

        // When
        val result = deleteStateUseCase(state)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
        verify(exactly = 1) { stateRepository.deleteState(state) }
    }

    @Test fun `invoke returns failure when repository throws`() {
        // Given
        val state = fakeData.createState()
        val ex = IllegalArgumentException("not found")
        every { stateRepository.deleteState(state) } throws ex

        // When
        val result = deleteStateUseCase(state)

        // Then
        assertTrue(result.isFailure)
        assertEquals(ex, result.exceptionOrNull())
    }
}